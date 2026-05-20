package com.studying.backendservice.services;

import com.studying.backendservice.dto.ProjectDTO;
import com.studying.backendservice.models.Project;
import com.studying.backendservice.repositories.CommentRepository;
import com.studying.backendservice.repositories.ProjectRepository;
import com.studying.backendservice.repositories.ProjectUserRepository;
import com.studying.backendservice.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

  ProjectRepository projectRepository;
  CommentRepository commentRepository;
  TaskRepository taskRepository;
  ProjectUserRepository projectUserRepository;

  @Autowired
  public ProjectServiceImpl(ProjectRepository projectRepository, CommentRepository commentRepository, TaskRepository taskRepository, ProjectUserRepository projectUserRepository) {
    this.projectRepository = projectRepository;
    this.commentRepository = commentRepository;
    this.taskRepository = taskRepository;
    this.projectUserRepository = projectUserRepository;
  }

  @Override
  public List<ProjectDTO> getAllProjectsForCurrentUser() {
    List<Project> projects = projectRepository.findAll();

    return projects.stream()
        .map(this::toDto)
        .collect(Collectors.toList());

  }

  @Override
  public ProjectDTO getProjectById(int id) {
    return projectRepository.findById(id)
        .map(this::toDto)
    .orElseThrow(() -> new EntityNotFoundException("Project not found"));
  }

  @Override
  public void addProject(ProjectDTO project) {
    Project projectEntity = new Project(project.getName(), project.getDescription());
    projectRepository.save(projectEntity);
  }

  @Override
  @Transactional
  public void updateProject(int id, ProjectDTO project) {
    Project updatedProject = projectRepository.findById(id).orElseThrow();
    updatedProject.setName(project.getName());
    updatedProject.setDescription(project.getDescription());
    projectRepository.save(updatedProject);

  }

  @Override
  public void deleteProject(int id) {
    projectRepository.deleteById(id);
  }

  public ProjectDTO toDto(Project project) {
    ProjectDTO dto = new ProjectDTO();
    dto.setId(project.getId());
    dto.setName(project.getName());
    dto.setDescription(project.getDescription());
    return dto;
  }
}
