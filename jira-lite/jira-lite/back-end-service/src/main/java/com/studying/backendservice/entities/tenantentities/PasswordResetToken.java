package com.studying.backendservice.entities.tenantentities;

import com.studying.backendservice.entities.userentity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

  @Setter
  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Setter
  @Getter
  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Setter
  @Getter
  @Column(nullable = false, unique = true)
  private String token;

  @Setter
  @Getter
  @Column(name = "expiry_date", nullable = false)
  private LocalDateTime expiryDate;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  public PasswordResetToken(String token, User user) {
    this.token = token;
    this.user = user;
  }

  public PasswordResetToken() {}
}

