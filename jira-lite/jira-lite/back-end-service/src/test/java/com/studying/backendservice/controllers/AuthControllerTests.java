package com.studying.backendservice.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.studying.backendservice.configurations.CommonExceptionHandler;
import com.studying.backendservice.configurations.TenantContext;
import com.studying.backendservice.dto.UserDTO;
import com.studying.backendservice.entities.userentity.User;
import com.studying.backendservice.services.JwtService;
import com.studying.backendservice.services.SchemaService;
import com.studying.backendservice.services.UserService;
import com.studying.backendservice.utils.Role;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

  @Mock
  AuthenticationManager authenticationManager;
  @Mock
  JwtService jwtService;
  @Mock
  UserService userService;
  @Mock
  PasswordEncoder passwordEncoder;
  @Mock
  JdbcTemplate jdbcTemplate;
  @Mock
  SchemaService schemaService;
  MockMvc mockMvc;

  @BeforeEach
  void setup() {
    AuthController controller = new AuthController(authenticationManager, jwtService,
        userService, passwordEncoder, jdbcTemplate, schemaService);
    mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(new CommonExceptionHandler())
        .build();
  }

  @AfterEach
  void clearTenantContext() {
    TenantContext.clear();
  }

  @Test
  void login_success_setsTenantContextAndReturnsJwtCookie() throws Exception {
    //given
    var userDto = new UserDTO(1, "test@test", "test@test",
        true, Role.ROLE_USER, "test123", "test_tenant");
    var user = new User("test@test", "test123",
        "test@test", "test_tenant", Role.ROLE_USER);
    var authResult = new UsernamePasswordAuthenticationToken(user,
        null, user.getAuthorities());
    when(userService.searchUsers("test@test"))
        .thenReturn(List.of(userDto));
    when(authenticationManager.authenticate(any(Authentication.class)))
        .thenReturn(authResult);
    when(jwtService.generateToken(any(), eq(user))).thenReturn("token_hash");
    var requestBody = """
        {"email":"test@test","password":"test123"}""";
    //when
    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
    //then
        .andExpect(status().isOk())
        .andExpect(cookie().exists("jwt"))
        .andExpect(cookie().value("jwt", "token_hash"));
  }

  @Test
  void login_fail_throwsEntityNotFoundException() throws Exception {
    //given
    when(userService.searchUsers("test@test"))
        .thenReturn(List.of());
    var requestBody = """
        {"email":"test@test","password":"test123"}""";
    //when
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        //then
        .andExpect(status().isNotFound());
  }

  @Test
  void logout_success_deletesJwtCookie() throws Exception {
    //given
    //when
    mockMvc.perform(post("/api/auth/logout"))
    //then
        .andExpect(status().isOk())
        .andExpect(content().string("Logged out"))
        .andExpect(cookie().exists("jwt"))
        .andExpect(cookie().value("jwt", ""))
        .andExpect(cookie().maxAge("jwt", 0))
        .andExpect(cookie().path("jwt", "/"))
        .andExpect(cookie().httpOnly("jwt", true));
  }

  @Test
  void register_success_createsNewUser() throws Exception {
    //given
    var userDto = new UserDTO(1, "test@test", "test@test",
        true, Role.ROLE_USER, "password_hash", "test_tenant");
    when(userService.searchUsers("test@test")).thenReturn(List.of());
    when(passwordEncoder.encode("test123")).thenReturn("password_hash");
    when(userService.toDto(any(User.class))).thenReturn(userDto);
    doNothing().when(userService).save(any(UserDTO.class));
    var requestBody = """
        {"email":"test@test","password":"test123"}""";
    //when
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        //then
        .andExpect(status().isOk())
        .andExpect(content().string("User registered successfully"));
  }

  @Test
  void register_fail_createsBadRequestResponseEntity() throws Exception {
    //given
    var userDto = new UserDTO(1, "test@test", "test@test",
        true, Role.ROLE_USER, "password_hash", "test_tenant");
    when(userService.searchUsers("test@test")).thenReturn(List.of(userDto));
    var requestBody = """
        {"email":"test@test","password":"test123"}""";
    //when
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        //then
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Username already exists"));
  }

}
