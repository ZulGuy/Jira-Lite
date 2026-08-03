package com.studying.backendservice.entities.userentity;


import com.studying.backendservice.utils.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
public class User implements UserDetails {

  @Getter
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Getter
  @Setter
  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Setter
  @Column(name = "username", unique = true, nullable = false)
  private String username;

  @Setter
  @Getter
  @Column(name = "password", nullable = false)
  private String password;

  @Getter
  @Setter
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role role;

  @Getter
  @Setter
  @Column(name = "tenant_name", nullable = false)
  @Pattern(
      regexp = "^[a-zA-Z0-9_]*$",
      message = "Tenant name can only contain letters, numbers, and underscores."
  )
  private String tenant = "public";

  @Column(name = "active", nullable = false)
  private boolean active = true;

  public User(String email, String password, String username, String tenant, Role role) {
    this.email = email;
    this.password = password;
    this.username = username;
    this.tenant = tenant;
    this.role = role;
  }

  protected User() {
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("USER"));
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() { return active; }

  public void setEnabled(boolean enabled) { this.active = enabled; }
}
