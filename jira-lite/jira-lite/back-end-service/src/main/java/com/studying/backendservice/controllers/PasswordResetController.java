package com.studying.backendservice.controllers;

import com.studying.backendservice.services.EmailService;
import com.studying.backendservice.services.PasswordResetService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

  private final PasswordResetService resetService;
  private final EmailService emailService;

  @Autowired
  public PasswordResetController(PasswordResetService resetService, EmailService emailService) {
    this.resetService = resetService;
    this.emailService = emailService;
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<?> sendResetLink(@RequestBody Map<String, String> payload) {
    String token = resetService.createResetToken(payload.get("email"));
    String resetLink = "http://localhost:4200/reset-password?token=" + token;
    emailService.sendHtmlEmail(payload.get("email"), "Reset Password", "Click here: " + resetLink);
    return ResponseEntity.ok().body(new HashMap<>() {{
      put("message", "Пароль змінено успішно.");
    }});
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> reset(@RequestBody Map<String, String> payload) {
    resetService.resetPassword(payload.get("token"), payload.get("newPassword"));
    return ResponseEntity.ok().body(new HashMap<>() {{
      put("message", "Пароль змінено успішно.");
    }});

  }
}

