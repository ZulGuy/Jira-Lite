package com.studying.backendservice.services;

import com.studying.backendservice.dto.UserDTO;
import com.studying.backendservice.models.ProjectUser;
import com.studying.backendservice.models.User;
import com.studying.backendservice.repositories.CommentRepository;
import com.studying.backendservice.repositories.ProjectRepository;
import com.studying.backendservice.repositories.ProjectUserRepository;
import com.studying.backendservice.repositories.TaskRepository;
import com.studying.backendservice.utils.ProjectRole;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {

  @Autowired
  private ProjectUserRepository projectUserRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private TaskRepository taskRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private CommentRepository commentRepository;

  public boolean haveAdminAccess(User principal) {
    return principal.getRole().name().equals("ROLE_ADMIN");
  }

  public boolean canManageProject(int projectId, User principal) {
    // 1. Системний адміністратор (User.getRole() == ADMIN)
    if (principal.getRole().name().equals("ROLE_ADMIN")) return true;

    // 2. Проектна роль ADMIN
    return projectUserRepository
        .findByProjectIdAndUserId(projectId, principal.getId())
        .map(pu -> pu.getRoles().contains(ProjectRole.ADMIN))
        .orElse(false);
  }


  public boolean canManageProjectOverload(int id, User principal) {
    // 1. Системний адміністратор (User.getRole() == ADMIN)
    if (principal.getRole().name().equals("ROLE_ADMIN")) return true;

    // 2. Проектна роль ADMIN
    return projectUserRepository
        .findByProjectIdAndUserId(id, principal.getId())
        .map(pu -> pu.getRoles().contains(ProjectRole.ADMIN))
        .orElse(false);
  }

  public boolean canAccessProject(int projectId, User principal) {
    // 1. Системний адміністратор (User.getRole() == ADMIN)
    if (principal.getRole().name().equals("ROLE_ADMIN")) return true;

    // 2. Проектна роль ADMIN
    return projectUserRepository
        .findByProjectIdAndUserId(projectId, principal.getId())
        .map(pu -> pu.getRoles().contains(ProjectRole.ADMIN)
            || pu.getRoles().contains(ProjectRole.EDITOR)
            || pu.getRoles().contains(ProjectRole.VIEWER) )
        .orElse(false);
  }

  @Transactional
  public boolean isAdminOrEditorByCommentId(int commentId) {
    UserDTO user = userService.getCurrentUser();
    if (user == null) return false;

    if (user.getRole().name().equals("ROLE_ADMIN")) return true;

    return commentRepository.findById(commentId)
        .map(comment -> comment.getTask().getProject().getId())
        .flatMap(projectId -> projectUserRepository.findByProjectIdAndUserId(projectId, user.getId()))
        .map(pu -> pu.getRoles().contains(ProjectRole.ADMIN) || pu.getRoles().contains(ProjectRole.EDITOR))
        .orElse(false);
  }

  @Transactional
  public boolean isAdminOrEditorByTaskId(int taskId) {
    UserDTO user = userService.getCurrentUser();
    if (user == null) return false;

    if (user.getRole().name().equals("ROLE_ADMIN")) return true;

    return taskRepository.findById(taskId)
        .flatMap(task -> projectUserRepository.findByProjectIdAndUserId(task.getProject().getId(), user.getId()))
        .map(pu -> pu.getRoles().contains(ProjectRole.ADMIN) || pu.getRoles().contains(ProjectRole.EDITOR))
        .orElse(false);
  }

}
