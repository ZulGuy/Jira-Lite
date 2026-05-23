package com.studying.backendservice.controllers;

import com.studying.backendservice.configurations.TenantContext;
import com.studying.backendservice.dto.UserDTO;
import com.studying.backendservice.services.InvitationService;
import com.studying.backendservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {
  private final InvitationService invitationService;
  private final UserService userService;

  @Autowired
  public InvitationController(InvitationService invitationService, UserService userService) {
    this.invitationService = invitationService;
    this.userService = userService;
  }

  @PostMapping("/send")
  @PreAuthorize("@securityService.haveAdminAccess(principal)")
  public ResponseEntity<?> send(@RequestParam String email) {
    try {
      invitationService.sendInvitation(email);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
    return ResponseEntity.ok("Запрошення надіслано");
  }

  @GetMapping("/validate")
  public ResponseEntity<Boolean> validate(@RequestParam String token) {
    return ResponseEntity.ok(invitationService.validateToken(token));
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestParam String token, @RequestBody UserDTO payload) {
    if (!invitationService.validateToken(token)) {
      return ResponseEntity.badRequest().body("Невалідний або використаний токен");
    }

    String email = invitationService.getEmailByToken(token);
    String tennant = invitationService.getTennantByToken(token);
    payload.setTennant(tennant);
    payload.setEmail(email);
    TenantContext.setTenantId(tennant);
    userService.register(payload);
    TenantContext.setTenantId("public");
    userService.register(payload);
    invitationService.markUsed(token);

    return ResponseEntity.ok("Користувача створено");
  }
}

