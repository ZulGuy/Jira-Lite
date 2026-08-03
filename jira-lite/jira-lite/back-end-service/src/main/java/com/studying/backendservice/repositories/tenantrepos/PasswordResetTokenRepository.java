package com.studying.backendservice.repositories.tenantrepos;

import com.studying.backendservice.entities.tenantentities.PasswordResetToken;
import com.studying.backendservice.entities.userentity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
  Optional<PasswordResetToken> findByToken(String token);
  void deleteByUser(User user);

}
