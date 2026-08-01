package com.studying.backendservice.services;

import com.studying.backendservice.dto.TaskDTO;
import com.studying.backendservice.entities.tenantentities.Task;
import java.util.List;

public interface TaskService {
  TaskDTO createTask(TaskDTO task);
  List<TaskDTO> getTasksForProject(int projectId);
  TaskDTO getTaskById(int id);
  void deleteTask(int id);
  TaskDTO updateTask(TaskDTO task);
  TaskDTO toDTO(Task task);

}
