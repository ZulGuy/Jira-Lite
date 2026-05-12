package com.studying.backendservice.services;

import com.studying.backendservice.dto.AddUserToProjectDTO;
import com.studying.backendservice.dto.ProjectUserDTO;
import com.studying.backendservice.dto.UpdateProjectRolesDTO;
import com.studying.backendservice.models.Project;
import com.studying.backendservice.models.ProjectUser;
import com.studying.backendservice.models.User;
import com.studying.backendservice.repositories.ProjectRepository;
import com.studying.backendservice.repositories.ProjectUserRepository;
import com.studying.backendservice.repositories.UserRepository;
import com.studying.backendservice.utils.ProjectRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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

  @PersistenceContext
  private EntityManager entityManager;


  @Autowired
  public ProjectUserServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, ProjectUserRepository projectUserRepository) {
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
  public void addUserToProject(int projectId, AddUserToProjectDTO dto) {
    if (projectUserRepository.existsByUserIdAndProjectId(dto.getUserId(), projectId)) {
      throw new IllegalStateException("Користувач вже доданий до цього проєкту.");
    }
    Project project = projectRepository.findById(projectId).orElseThrow();
    User user = userRepository.findById(dto.getUserId()).orElseThrow();

    ProjectUser pu = new ProjectUser();
    pu.setProject(project);
    pu.setUser(user);
    pu.setRoles(dto.getRoles());

    projectUserRepository.save(pu);
  }

  @Override
  @Transactional
  public void updateRoles(int projectId, int userId, UpdateProjectRolesDTO dto) {
    ProjectUser pu = projectUserRepository.findByProjectIdAndUserId(projectId, userId)
        .orElseThrow();

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
    ProjectUserDTO dto = new ProjectUserDTO();
    dto.setId(u.getId());
    dto.setName(u.getUsername());
    dto.setEmail(u.getEmail());
    dto.setActive(u.isEnabled());
    dto.setRoles(pu.getRoles());
    return dto;
  }
}