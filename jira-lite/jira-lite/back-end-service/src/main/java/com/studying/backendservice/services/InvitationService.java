package com.studying.backendservice.services;

import com.studying.backendservice.configurations.TenantContext;
import com.studying.backendservice.entities.tenantentities.InvitationToken;
import com.studying.backendservice.repositories.tenantrepos.InvitationTokenRepository;
import com.studying.backendservice.repositories.tenantrepos.UserRepository;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class InvitationService {

  private final InvitationTokenRepository tokenRepo;
  private final JavaMailSender mailSender;
  private final UserRepository userRepository;

  @Autowired
  public InvitationService(InvitationTokenRepository tokenRepo, JavaMailSender mailSender,
      UserRepository userRepository) {
    this.tokenRepo = tokenRepo;
    this.mailSender = mailSender;
    this.userRepository = userRepository;
  }

  public void sendInvitation(String email) {
    String currentTenant = TenantContext.getTenantId();
    TenantContext.setTenantId("public");
    if (userRepository.existsByEmail(email) && userRepository.findByEmail(email).isPresent()
        && !userRepository.findByEmail(email).get().getTenant().equals("public")) {
      throw new IllegalStateException("Користувач з таким email вже зареєстрований.");
    }
    if (tokenRepo.existsByEmailAndUsedFalse(email)) {
      throw new IllegalArgumentException("Запрошення вже було надіслано");
    }

    String token = UUID.randomUUID().toString();
    InvitationToken invitation = new InvitationToken();
    invitation.setEmail(email);
    invitation.setToken(token);
    invitation.setExpiresAt(LocalDateTime.now().plusDays(2));
    invitation.setUsed(false);
    invitation.setTennant(currentTenant);

    tokenRepo.save(invitation);

    String link = "http://localhost:80/register-invite?token=" + token;

    String html = "<h3>Запрошення до системи</h3>"
        + "<p>Для завершення реєстрації перейдіть за посиланням:</p>"
        + "<a href=\"" + link + "\">" + link + "</a>";

    sendHtmlEmail(email, "Запрошення до системи", html);
    TenantContext.setTenantId(currentTenant);
  }

  public boolean validateToken(String token) {
    String currentTenant = TenantContext.getTenantId();
    TenantContext.setTenantId("public");
    boolean result = tokenRepo.findByToken(token)
        .filter(t -> !t.isUsed() && t.getExpiresAt().isAfter(LocalDateTime.now()))
        .isPresent();
    TenantContext.setTenantId(currentTenant);
    return result;
  }

  public String getEmailByToken(String token) {
    String currentTenant = TenantContext.getTenantId();
    TenantContext.setTenantId("public");
    String email = tokenRepo.findByToken(token).map(InvitationToken::getEmail)
        .orElseThrow(() -> new IllegalArgumentException("Невалідний токен"));
    TenantContext.setTenantId(currentTenant);
    return email;
  }

  public String getTennantByToken(String token) {
    String currentTenant = TenantContext.getTenantId();
    TenantContext.setTenantId("public");
    String tenant = tokenRepo.findByToken(token).map(InvitationToken::getTennant)
        .orElseThrow(() -> new IllegalArgumentException("Невалідний токен"));
    TenantContext.setTenantId(currentTenant);
    return tenant;
  }

  public void markUsed(String token) {
    String currentTenant = TenantContext.getTenantId();
    TenantContext.setTenantId("public");
    InvitationToken invitation = tokenRepo.findByToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Невалідний токен"));
    invitation.setUsed(true);
    tokenRepo.save(invitation);
    TenantContext.setTenantId(currentTenant);
  }

  private void sendHtmlEmail(String to, String subject, String htmlContent) {
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);
      mailSender.send(message);
    } catch (Exception e) {
      System.out.println("Помилка при надсиланні запрошення: " + e);
    }
  }
}


