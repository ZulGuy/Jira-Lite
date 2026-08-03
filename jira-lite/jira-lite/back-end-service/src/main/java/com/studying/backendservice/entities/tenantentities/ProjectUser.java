package com.studying.backendservice.entities.tenantentities;

import com.studying.backendservice.entities.userentity.User;
import com.studying.backendservice.utils.ProjectRole;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import lombok.Setter;

@Entity
@Table(name = "project_users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "project_id"})
})
public class ProjectUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "project_id")
  private Project project;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Setter
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "project_user_roles",
      joinColumns = @JoinColumn(name = "project_user_id")
  )
  @Column(name = "role")
  private Set<ProjectRole> roles = new HashSet<>();

  protected ProjectUser() {}

  public ProjectUser(Project project, User user, Set<ProjectRole> roles) {
    this.project = project;
    this.user = user;
    this.roles = roles;
  }

  public int getId() {
    return id;
  }

  public Project getProject() {
    return project;
  }

  public User getUser() {
    return user;
  }

  public Set<ProjectRole> getRoles() {
    return roles;
  }
}
