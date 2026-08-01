package com.studying.backendservice.repositories.tenantrepos;

import com.studying.backendservice.entities.tenantentities.InvitationToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationTokenRepository extends JpaRepository<InvitationToken, Long> {
  Optional<InvitationToken> findByToken(String token);
  boolean existsByEmailAndUsedFalse(String email);
}


