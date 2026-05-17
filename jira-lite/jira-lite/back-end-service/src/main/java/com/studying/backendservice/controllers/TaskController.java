package com.studying.backendservice.controllers;

import com.studying.backendservice.dto.CommentDTO;
import com.studying.backendservice.dto.TaskDTO;
import com.studying.backendservice.models.Project;
import com.studying.backendservice.models.Task;
import com.studying.backendservice.models.User;
import com.studying.backendservice.repositories.ProjectRepository;
import com.studying.backendservice.repositories.TaskRepository;
import com.studying.backendservice.repositories.UserRepository;
import com.studying.backendservice.services.EmailService;
import com.studying.backendservice.services.TaskService;
import com.studying.backendservice.utils.Status;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;
  private final EmailService emailService;

  @Autowired
  public TaskController(TaskService taskService, EmailService emailService) {
    this.taskService = taskService;
    this.emailService = emailService;
  }

  @PostMapping
  public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO dto, @AuthenticationPrincipal User userDetails) {

    TaskDTO createdTask = taskService.createTask(dto);
    emailService.sendTaskCreatedNotification(createdTask);

    return ResponseEntity.ok(dto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskDTO> getTask(@PathVariable int id) {
    TaskDTO task = taskService.getTaskById(id);

    task.setComments(task.getComments().stream().map(comment -> {
      CommentDTO c = new CommentDTO();
      c.setId(comment.getId());
      c.setDescription(comment.getDescription());
      c.setAuthorName(comment.getAuthorName() != null ? comment.getAuthorName() : "Анонім");
      return c;
    }).toList());

    return ResponseEntity.ok(task);
  }

  @PreAuthorize("@securityService.isAdminOrEditorByTaskId(#id)")
  @PutMapping("/{id}")
  public ResponseEntity<TaskDTO> updateTask(@PathVariable int id, @RequestBody TaskDTO dto) {
    return ResponseEntity.ok(taskService.updateTask(dto));
  }

  @PreAuthorize("@securityService.isAdminOrEditorByTaskId(#id)")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable int id) {
    taskService.deleteTask(id);
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("@securityService.canAccessProject(#projectId, principal)")
  @GetMapping("/project/{projectId}")
  public ResponseEntity<List<TaskDTO>> getTasksForProject(@PathVariable int projectId) {
    List<TaskDTO> tasks = taskService.getTasksForProject(projectId);
    return ResponseEntity.ok(tasks);
  }


}
