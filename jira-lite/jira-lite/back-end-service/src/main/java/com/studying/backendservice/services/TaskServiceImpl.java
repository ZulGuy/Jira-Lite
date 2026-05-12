package com.studying.backendservice.services;

import com.studying.backendservice.dto.TaskDTO;
import com.studying.backendservice.models.Project;
import com.studying.backendservice.models.Task;
import com.studying.backendservice.models.TaskType;
import com.studying.backendservice.repositories.ProjectRepository;
import com.studying.backendservice.repositories.TaskRepository;
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
  private final TaskTypeServiceImpl taskTypeService;

  @Autowired
  public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository, TaskTypeServiceImpl taskTypeService) {
    this.taskRepository = taskRepository;
    this.projectRepository = projectRepository;
    this.taskTypeService = taskTypeService;
  }

  @Override
  public Task createTask(Task task) {
    if (task.getStatus() == null) {
      task.setStatus(Status.TODO);
    }
    return taskRepository.save(task);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Task> getTasksForProject(int projectId) {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    return project.getTasks();
  }

  @Override
  public Task getTaskById(int id) {
    return taskRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Task not found"));
  }

  @Override
  public void deleteTask(int id) {
    taskRepository.deleteById(id);
  }

  @Override
  public Task updateTask(Task task) {
    Task existing = taskRepository.findById(task.getId())
        .orElseThrow(() -> new EntityNotFoundException("Task not found"));

    existing.setSummary(task.getSummary());
    existing.setDescription(task.getDescription());

    if (task.getStatus() != null) {
      existing.setStatus(task.getStatus());
    }

    if (task.getAssignee() != null) {
      existing.setAssignee(task.getAssignee());
    }

    if (task.getInitiator() != null) {
      existing.setInitiator(task.getInitiator());
    }

    System.out.println("Updating task " + task.getId() + " with status: " + task.getStatus());

    return taskRepository.save(existing);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Task> getTasksByTaskTypeName(String name) {
    TaskType taskType = this.taskTypeService.getTaskTypeByName(name);
    return taskType.getTasks();
  }
}
