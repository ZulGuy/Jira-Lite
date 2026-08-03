package com.studying.backendservice.entities.tenantentities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.studying.backendservice.entities.userentity.User;
import com.studying.backendservice.utils.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "tasks")
public class Task {

  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Setter
  @Getter
  @ManyToOne
  @JoinColumn(name = "project_id", nullable = false)
  @JsonBackReference
  private Project project;

  @Setter
  @Getter
  @Column(name = "summary", nullable = false)
  private String summary;

  @Setter
  @Getter
  @Column(name = "description", nullable = false)
  private String description;

  @Setter
  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("comment-task")
  private List<Comment> comments = new ArrayList<Comment>();

  @Setter
  @Getter
  @ManyToOne
  @JoinColumn(name = "assignee_id")
  private User assignee;

  @Setter
  @Getter
  @ManyToOne()
  @JoinColumn(name = "initiator_id")
  private User initiator;

  @Setter
  @Getter
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private Status status;


  @Getter
  @Column(name = "created_at")
  private final LocalDateTime createdAt = LocalDateTime.now();

  protected Task() {
  }

  public Task(String summary, String description, Project project, User assignee,
      User initiator, Status status) {
    this.summary = summary;
    this.description = description;
    this.project = project;
    this.assignee = assignee;
    this.initiator = initiator;
    this.status = status;
  }

  public Collection<Comment> getComments() {
    return comments;
  }
}
