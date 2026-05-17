package com.studying.backendservice.controllers;

import com.studying.backendservice.dto.AddUserToProjectDTO;
import com.studying.backendservice.dto.ProjectUserDTO;
import com.studying.backendservice.dto.UpdateProjectRolesDTO;
import com.studying.backendservice.services.ProjectUserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/users")
public class ProjectUserController {

  private final ProjectUserService projectUserService;

  @Autowired
  public ProjectUserController(ProjectUserService projectUserService) {
    this.projectUserService = projectUserService;
  }

  @PreAuthorize("@securityService.canManageProject(#projectId, principal)")
  @GetMapping
  public List<ProjectUserDTO> list(@PathVariable int projectId) {
    return projectUserService.getUsersForProject(projectId);
  }

  @PreAuthorize("@securityService.canManageProject(#projectId, principal)")
  @PostMapping
  public void addUser(@PathVariable int projectId, @RequestBody @Valid AddUserToProjectDTO dto) {
    System.out.println("Received addUserToProject: " + dto.userId() + " roles: " + dto.roles());
    projectUserService.addUserToProject(projectId, dto);
  }

  @PreAuthorize("@securityService.canManageProject(#projectId, principal)")
  @PutMapping("/{userId}/roles")
  public void updateRole(@PathVariable int projectId, @PathVariable int userId, @RequestBody UpdateProjectRolesDTO dto) {
    projectUserService.updateRoles(projectId, userId, dto);
  }

  @PreAuthorize("@securityService.canManageProject(#projectId, principal)")
  @DeleteMapping("/{userId}")
  public void deleteUser(@PathVariable int projectId, @PathVariable int userId) {
    projectUserService.removeUserFromProject(projectId, userId);
  }

}
