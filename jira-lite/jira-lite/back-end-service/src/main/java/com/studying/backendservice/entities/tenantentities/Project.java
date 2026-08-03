package com.studying.backendservice.entities.tenantentities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;

@Entity
@Table(name = "projects")
public class Project {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Setter
  @Column(name = "name", unique = true, nullable = false)
  private String name;

  @Setter
  @Column(name = "description", unique = true)
  private String description;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
  private List<ProjectUser> projectUsers = new ArrayList<>();

  @Setter
  @JsonManagedReference
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
  private List<Task> tasks = new ArrayList<Task>();

  protected Project() {}

  public Project(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public List<Task> getTasks() {
    return tasks;
  }

  public List<ProjectUser> getProjectUsers() {
    return projectUsers;
  }

  public void setProjectUsers(List<ProjectUser> projectUsers) {
    this.projectUsers = projectUsers;
  }
}
