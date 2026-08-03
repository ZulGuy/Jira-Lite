package com.studying.backendservice.repositories.tenantrepos;

import com.studying.backendservice.entities.userentity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<List<User>> findByUsernameContainingIgnoreCase(String username);
  Optional<User> findByUsername(String username);
  boolean existsByEmail(String email);
  Optional<User> findByEmail(String email);
}
