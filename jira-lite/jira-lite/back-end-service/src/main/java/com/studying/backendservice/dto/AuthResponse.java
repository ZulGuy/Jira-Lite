package com.studying.backendservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public record AuthResponse(@JsonProperty("token") String token) implements Serializable {

  public AuthResponse(String token) {
    this.token = token;
  }

  @Override
  public String token() {
    return token;
  }
}
