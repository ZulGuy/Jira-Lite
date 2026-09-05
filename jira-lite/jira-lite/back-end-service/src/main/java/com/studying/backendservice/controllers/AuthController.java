package com.studying.backendservice.controllers;

import com.studying.backendservice.configurations.TenantContext;
import com.studying.backendservice.dto.AuthRequest;
import com.studying.backendservice.dto.AuthResponse;
import com.studying.backendservice.dto.UserDTO;
import com.studying.backendservice.entities.userentity.User;
import com.studying.backendservice.services.JwtService;
import com.studying.backendservice.services.SchemaService;
import com.studying.backendservice.services.UserService;
import com.studying.backendservice.utils.Role;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JdbcTemplate jdbcTemplate;
  private final SchemaService schemaService;

  @Autowired
  public AuthController(
      AuthenticationManager authenticationManager, JwtService jwtService,
      UserService userService, PasswordEncoder passwordEncoder,
      JdbcTemplate jdbcTemplate, SchemaService schemaService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jdbcTemplate = jdbcTemplate;
    this.schemaService = schemaService;
  }

  @PostMapping(value = "/login",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    setTenant("public");
    String tenant;
    try {
       tenant = userService.searchUsers(request.getEmail()).get(0).getTennant();
    } catch (IndexOutOfBoundsException e) {
      throw new EntityNotFoundException(e);
    }
    setTenant(tenant);
    log.info("request login {}", request.getEmail());
    log.info("TenantContext: {}", TenantContext.getTenantId());
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );
    log.info("request login {}", request.getEmail());
    var user = (User) authentication.getPrincipal();
    Map<String, Object> claims = new HashMap<>();
    claims.put("tennantId", user.getTenant());
    claims.put("role", user.getAuthorities());
    String token = jwtService.generateToken(claims, user);
    ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(24 * 60 * 60)
        .sameSite("Lax")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(new AuthResponse(token));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(0)
        .sameSite("Lax")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body("Logged out");
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody AuthRequest request) {
    setTenant("public");

    if (!userService.searchUsers(request.getEmail()).isEmpty()) {
      return ResponseEntity.badRequest().body("Username already exists");
    }

    User newUser = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()),
        request.getEmail(), "public", Role.ROLE_USER);
    newUser.setEnabled(true);
    UserDTO dto = userService.toDto(newUser);

    userService.save(dto);
    return ResponseEntity.ok("User registered successfully");
  }

  @GetMapping("/switch-tenant")
  public ResponseEntity<String> switchTenant() {
    Map<String, Object> claimsMap = new HashMap<>();
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    claimsMap.put("role", user.getAuthorities());

    if (TenantContext.getTenantId().equals("public")) {
      UserDTO dto = userService.searchUsers(userService.getCurrentUser().getEmail()).get(0);
      if (dto.getTennant().equals("public")) {
        return ResponseEntity.badRequest().body("User is not in any tennant");
      }
      claimsMap.put("tennantId", dto.getTennant());
      setTenant(dto.getTennant());
      String token = jwtService.generateToken(claimsMap, user);
      ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
          .httpOnly(true)
          .secure(false)
          .path("/")
          .maxAge(24 * 60 * 60)
          .sameSite("Lax")
          .build();
      return ResponseEntity.ok()
          .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
          .body("Tenant switched to " + TenantContext.getTenantId());
    }
    claimsMap.put("tennantId", "public");
    setTenant("public");
    String token = jwtService.generateToken(claimsMap, user);
    ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(24 * 60 * 60)
        .sameSite("Lax")
        .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body("Tenant switched to " + TenantContext.getTenantId());
  }

  private void setTenant(String tenant) {
    for (String schema : schemaService.getSchemas()) {
      if (tenant.equals(schema) && !(tenant.equals("pg_catalog")
          || tenant.equals("information_schema") || tenant.equals("pg_toast"))) {
        TenantContext.setTenantId(tenant);
        return;
      }
    }
    TenantContext.setTenantId("public");
  }
}
