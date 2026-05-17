package com.studying.backendservice.dto;

import com.studying.backendservice.utils.ProjectRole;
import java.util.Set;

public class ProjectUserDTO {

  private int id;
  private String name;
  private String email;
  private boolean active;
  private Set<ProjectRole> roles;

  public ProjectUserDTO(Set<ProjectRole> roles, boolean active, String email, String name, int id) {
    this.roles = roles;
    this.active = active;
    this.email = email;
    this.name = name;
    this.id = id;
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

  public boolean isActive() {
    return active;
  }

  public Set<ProjectRole> getRoles() {
    return roles;
  }
}
