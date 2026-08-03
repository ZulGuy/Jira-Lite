package com.studying.backendservice.entities.tenantentities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.studying.backendservice.entities.userentity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Setter;

@Entity
@Table(name = "comments")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "task_id", nullable = false)
  @JsonBackReference("comment-task")
  private Task task;

  @Setter
  @Column(name = "description", nullable = false)
  private String description;

  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  @JsonBackReference
  private User author;

  protected Comment() {}

  public Comment(String description, User author, Task task) {
    this.description = description;
    this.author = author;
    this.task = task;
  }

  public String getDescription() {
    return description;
  }

  public User getAuthor() {
    return author;
  }


  public int getId() {
    return id;
  }

  public Task getTask() {
    return task;
  }
}
