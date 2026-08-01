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

@Entity
@Table(name = "projects")
public class Project {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "name", unique = true, nullable = false)
  private String name;

  @Column(name = "description", unique = true)
  private String description;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
  private List<ProjectUser> projectUsers = new ArrayList<>();

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

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
  }

  public List<ProjectUser> getProjectUsers() {
    return projectUsers;
  }

  public void setProjectUsers(List<ProjectUser> projectUsers) {
    this.projectUsers = projectUsers;
  }
}
