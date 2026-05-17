package com.studying.backendservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import java.io.Serializable;

public class AuthRequest implements Serializable {

  @JsonProperty("email")
  @Email
  private String email;
  @JsonProperty("password")
  private String password;

  public AuthRequest() {
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
