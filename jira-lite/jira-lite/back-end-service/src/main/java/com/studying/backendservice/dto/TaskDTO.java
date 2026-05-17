package com.studying.backendservice.dto;

import com.studying.backendservice.utils.Status;
import java.util.List;

public class TaskDTO {
  private int id;
  private String summary;
  private String description;
  private int assigneeId;
  private int initiatorId;
  private Status status;
  private int projectId;
  private List<CommentDTO> comments;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Integer getAssigneeId() {
    return assigneeId;
  }

  public void setAssigneeId(Integer assigneeId) {
    this.assigneeId = assigneeId;
  }

  public Integer getInitiatorId() {
    return initiatorId;
  }

  public void setInitiatorId(Integer initiatorId) {
    this.initiatorId = initiatorId;
  }

  public void setAssigneeId(int assigneeId) {
    this.assigneeId = assigneeId;
  }

  public void setInitiatorId(int initiatorId) {
    this.initiatorId = initiatorId;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public List<CommentDTO> getComments() {
    return comments;
  }

  public void setComments(List<CommentDTO> comments) {
    this.comments = comments;
  }
}

