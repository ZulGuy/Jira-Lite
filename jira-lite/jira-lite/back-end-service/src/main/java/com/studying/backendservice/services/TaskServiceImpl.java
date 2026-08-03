package com.studying.backendservice.services;

import com.studying.backendservice.dto.CommentDTO;
import com.studying.backendservice.dto.TaskDTO;
import com.studying.backendservice.entities.tenantentities.Comment;
import com.studying.backendservice.entities.tenantentities.Project;
import com.studying.backendservice.entities.tenantentities.Task;
import com.studying.backendservice.entities.userentity.User;
import com.studying.backendservice.repositories.tenantrepos.ProjectRepository;
import com.studying.backendservice.repositories.tenantrepos.TaskRepository;
import com.studying.backendservice.repositories.tenantrepos.UserRepository;
import com.studying.backendservice.utils.Status;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;

  @Autowired
  public TaskServiceImpl(TaskRepository taskRepository,
      ProjectRepository projectRepository, UserRepository userRepository) {
    this.taskRepository = taskRepository;
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
  }

  @Override
  public TaskDTO createTask(TaskDTO task) {
    Project project = projectRepository.findById(task.getProjectId()).orElseThrow(
        () -> new EntityNotFoundException("Project not found")
    );
    User assignee = null;
    if (task.getAssigneeId() != null) {
      assignee = userRepository.findById(task.getAssigneeId()).orElseThrow(
          () -> new EntityNotFoundException("Assignee not found")
      );
    }
    User initiator = userRepository.findById(task.getInitiatorId()).orElseThrow(
        () -> new EntityNotFoundException("Initiator not found")
    );
    Task newTask = new Task(task.getSummary(), task.getDescription(), project, assignee, initiator,
        task.getStatus());

    if (task.getStatus() == null) {
      newTask.setStatus(Status.TODO);
    }
    return toDTO(taskRepository.save(newTask));
  }

  @Override
  @Transactional(readOnly = true)
  public List<TaskDTO> getTasksForProject(int projectId) {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    return project.getTasks().stream()
        .map(this::toDTO)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public TaskDTO getTaskById(int id) {
    return taskRepository.findById(id)
        .map(this::toDTO)
        .orElseThrow(() -> new EntityNotFoundException("Task not found"));
  }

  @Override
  public void deleteTask(int id) {
    taskRepository.deleteById(id);
  }

  @Override
  public TaskDTO updateTask(TaskDTO task) {
    Task existing = taskRepository.findById(task.getId())
        .orElseThrow(() -> new EntityNotFoundException("Task not found"));

    existing.setSummary(task.getSummary());
    existing.setDescription(task.getDescription());
    User assignee = null;
    User initiator = null;

    if (task.getStatus() != null) {
      existing.setStatus(task.getStatus());
    }

    if (task.getAssigneeId() != null) {
      assignee = userRepository.findById(task.getAssigneeId()).orElseThrow(
          () -> new EntityNotFoundException("Assignee not found")
      );
    }
    existing.setAssignee(assignee);

    if (task.getInitiatorId() != null) {
      initiator = userRepository.findById(task.getInitiatorId()).orElseThrow(
          () -> new EntityNotFoundException("Initiator not found")
      );
    }
    existing.setInitiator(initiator);

    System.out.println("Updating task " + task.getId() + " with status: " + task.getStatus());

    return toDTO(taskRepository.save(existing));
  }

  public TaskDTO toDTO(Task task) {
    TaskDTO dto = new TaskDTO();
    dto.setId(task.getId());
    dto.setSummary(task.getSummary());
    dto.setDescription(task.getDescription());
    dto.setStatus(task.getStatus());
    dto.setProjectId(task.getProject().getId());
    dto.setAssigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null);
    dto.setInitiatorId(task.getInitiator() != null ? task.getInitiator().getId() : null);
    dto.setComments(task.getComments().stream()
        .map(this::toCommentDto)
        .toList());
    return dto;
  }

  public CommentDTO toCommentDto(Comment comment) {
    CommentDTO dto = new CommentDTO();
    dto.setId(comment.getId());
    dto.setDescription(comment.getDescription());
    dto.setAuthorName(comment.getAuthor().getEmail());
    return dto;
  }
}
