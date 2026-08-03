package com.studying.backendservice.services;

import com.studying.backendservice.dto.AddUserToProjectDTO;
import com.studying.backendservice.dto.ProjectUserDTO;
import com.studying.backendservice.dto.UpdateProjectRolesDTO;
import com.studying.backendservice.entities.tenantentities.Project;
import com.studying.backendservice.entities.tenantentities.ProjectUser;
import com.studying.backendservice.entities.userentity.User;
import com.studying.backendservice.repositories.tenantrepos.ProjectRepository;
import com.studying.backendservice.repositories.tenantrepos.ProjectUserRepository;
import com.studying.backendservice.repositories.tenantrepos.UserRepository;
import com.studying.backendservice.utils.ProjectRole;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectUserServiceImpl implements ProjectUserService {

  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final ProjectUserRepository projectUserRepository;

  @Autowired
  public ProjectUserServiceImpl(ProjectRepository projectRepository, UserRepository userRepository,
      ProjectUserRepository projectUserRepository) {
    this.projectUserRepository = projectUserRepository;
    this.userRepository = userRepository;
    this.projectRepository = projectRepository;
  }

  @Override
  public List<ProjectUserDTO> getUsersForProject(int projectId) {
    return projectUserRepository.findByProjectId(projectId).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void addUserToProject(int projectId, @Valid AddUserToProjectDTO dto) {
    if (projectUserRepository.existsByUserIdAndProjectId(dto.userId(), projectId)) {
      throw new IllegalStateException("User was already added to the project.");
    }
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));
    User user = userRepository.findById(dto.userId())
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + dto.userId()));
    ProjectUser pu = new ProjectUser(project, user, dto.roles());
    projectUserRepository.save(pu);
  }

  @Override
  @Transactional
  public void updateRoles(int projectId, int userId, UpdateProjectRolesDTO dto) {
    ProjectUser pu = projectUserRepository.findByProjectIdAndUserId(projectId, userId)
        .orElseThrow(() -> new EntityNotFoundException("No such project user: " + projectId + ", " + userId));

    Set<ProjectRole> newRoles = dto.getRoles() != null ? dto.getRoles() : new java.util.HashSet<>();

    if (newRoles.isEmpty()) {
      projectUserRepository.deleteById(pu.getId());
    } else {
      pu.setRoles(newRoles);
      projectUserRepository.save(pu);
    }
  }


  @Override
  public void removeUserFromProject(int projectId, int userId) {
    projectUserRepository.deleteByProjectIdAndUserId(projectId, userId);
  }

  private ProjectUserDTO toDto(ProjectUser pu) {
    User u = pu.getUser();
    return new ProjectUserDTO(pu.getRoles(), u.isEnabled(), u.getEmail()
        ,u.getUsername(),u.getId());
  }
}