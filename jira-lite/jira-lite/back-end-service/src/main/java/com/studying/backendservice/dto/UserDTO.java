package com.studying.backendservice.dto;

import com.studying.backendservice.utils.Role;

public class UserDTO {

  private int id;
  private String name;
  private String email;
  private boolean active;
  private Role role;
  private String password;

  private String tennant;

  public UserDTO(int id, String name, String email, boolean active, Role role, String password,
      String tennant) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.active = active;
    this.role = role;
    this.password = password;
    this.tennant = tennant;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String getPassword() {
    return password;
  }

  public String getTennant() {
    return tennant;
  }

  public void setTennant(String tennant) {
    this.tennant = tennant;
  }
}
