package com.studying.backendservice.services;

import com.studying.backendservice.models.InvitationToken;
import com.studying.backendservice.repositories.InvitationTokenRepository;
import com.studying.backendservice.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
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
  public InvitationService(InvitationTokenRepository tokenRepo, JavaMailSender mailSender, UserRepository userRepository) {
    this.tokenRepo = tokenRepo;
    this.mailSender = mailSender;
    this.userRepository = userRepository;
  }

  @Transactional
  public void sendInvitation(String email) {
    if (userRepository.existsByEmail(email)) {
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

    tokenRepo.save(invitation);

    String link = "http://localhost:4200/register?token=" + token;

    String html = "<h3>Запрошення до системи</h3>"
        + "<p>Для завершення реєстрації перейдіть за посиланням:</p>"
        + "<a href=\"" + link + "\">" + link + "</a>";

    sendHtmlEmail(email, "Запрошення до системи", html);
  }

  public boolean validateToken(String token) {
    return tokenRepo.findByToken(token)
        .filter(t -> !t.isUsed() && t.getExpiresAt().isAfter(LocalDateTime.now()))
        .isPresent();
  }

  public String getEmailByToken(String token) {
    return tokenRepo.findByToken(token).map(InvitationToken::getEmail)
        .orElseThrow(() -> new IllegalArgumentException("Невалідний токен"));
  }

  public String getTennantByToken(String token) {
    return tokenRepo.findByToken(token).map(InvitationToken::getTennant)
        .orElseThrow(() -> new IllegalArgumentException("Невалідний токен"));
  }

  @Transactional
  public void markUsed(String token) {
    InvitationToken invitation = tokenRepo.findByToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Невалідний токен"));
    invitation.setUsed(true);
    tokenRepo.save(invitation);
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
      throw new RuntimeException("Помилка надсилання пошти", e);
    }
  }
}


