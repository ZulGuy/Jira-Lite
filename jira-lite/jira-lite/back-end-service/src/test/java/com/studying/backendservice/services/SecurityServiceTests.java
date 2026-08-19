package com.studying.backendservice.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.studying.backendservice.entities.tenantentities.Project;
import com.studying.backendservice.entities.tenantentities.ProjectUser;
import com.studying.backendservice.entities.userentity.User;
import com.studying.backendservice.repositories.tenantrepos.CommentRepository;
import com.studying.backendservice.repositories.tenantrepos.ProjectUserRepository;
import com.studying.backendservice.repositories.tenantrepos.TaskRepository;
import com.studying.backendservice.utils.ProjectRole;
import com.studying.backendservice.utils.Role;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTests {

  @Mock UserService userService;
  @Mock ProjectUserRepository projectUserRepository;
  @Mock TaskRepository taskRepository;
  @Mock CommentRepository commentRepository;
  @InjectMocks SecurityService securityService;

  @Test
  void haveAdminAccess_true_returnsTrue() {
    //given
    var user = new User("test@test", "test123",
        "test@test", "test_tenant", Role.ROLE_ADMIN);
    //when
    boolean result = securityService.haveAdminAccess(user);
    //then
    assertThat(result).isEqualTo(true);
  }

  @Test
  void canManageProject_true_returnsTrueForAdmin() {
    //given
    var user = new User("test@test", "test123",
        "test@test", "test_tenant", Role.ROLE_ADMIN);
    //when
    boolean result = securityService.canManageProject(1, user);
    //then
    assertThat(result).isEqualTo(true);
  }

  @Test
  void canManageProject_true_returnsTrueForProjectAdmin() {
    //given
    var user = new User("test@test", "test123",
        "test@test", "test_tenant", Role.ROLE_USER);
    var projectUser = new ProjectUser(new Project("test", "test"), user, Set.of(ProjectRole.ADMIN));
    when(projectUserRepository.findByProjectIdAndUserId(1, user.getId()))
        .thenReturn(Optional.of(projectUser));
    //when
    boolean result = securityService.canManageProject(1, user);
    //then
    assertThat(result).isEqualTo(true);
  }

  @Test
  void haveAdminAccess_false_returnsFalse() {
    //given
    var user = new User("test@test", "test123",
        "test@test", "test_tenant", Role.ROLE_USER);
    //when
    boolean result = securityService.haveAdminAccess(user);
    //then
    assertThat(result).isEqualTo(false);
  }

  @Test
  void canManageProject_false_returnsFalseForProjectAdmin() {
    //given
    var user = new User("test@test", "test123",
        "test@test", "test_tenant", Role.ROLE_USER);
    var projectUser = new ProjectUser(new Project("test", "test"), user, Set.of(ProjectRole.VIEWER));
    when(projectUserRepository.findByProjectIdAndUserId(1, user.getId()))
        .thenReturn(Optional.of(projectUser));
    //when
    boolean result = securityService.canManageProject(1, user);
    //then
    assertThat(result).isEqualTo(false);
  }
}
