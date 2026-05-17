package com.studying.backendservice.services;

import com.studying.backendservice.models.PasswordResetToken;
import com.studying.backendservice.models.User;
import com.studying.backendservice.repositories.PasswordResetTokenRepository;
import com.studying.backendservice.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

  private final PasswordResetTokenRepository tokenRepo;
  private final UserRepository userRepo;

  @Autowired
  public PasswordResetService(PasswordResetTokenRepository tokenRepo, UserRepository userRepo) {
    this.tokenRepo = tokenRepo;
    this.userRepo = userRepo;
  }

  @Transactional
  public String createResetToken(String email) {
    User user = userRepo.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    tokenRepo.deleteByUser(user);

    String token = UUID.randomUUID().toString();
    LocalDateTime expiry = LocalDateTime.now().plusHours(2);

    PasswordResetToken resetToken = new PasswordResetToken();
    resetToken.setUser(user);
    resetToken.setToken(token);
    resetToken.setExpiryDate(expiry);

    tokenRepo.save(resetToken);

    System.out.println("Token created: " + token + " for user: " + user.getEmail());

    return token;
  }


  public boolean isValidToken(String token) {
    return tokenRepo.findByToken(token)
        .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
        .isPresent();
  }

  @Transactional
  public void resetPassword(String token, String newPassword) {
    PasswordResetToken resetToken = tokenRepo.findByToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

    if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("Token expired");
    }

    User user = resetToken.getUser();
    user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
    userRepo.save(user);

    tokenRepo.delete(resetToken);
  }
}

