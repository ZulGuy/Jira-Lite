package com.studying.backendservice.repositories.tenantrepos;

import com.studying.backendservice.entities.tenantentities.ProjectUser;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectUserRepository extends JpaRepository<ProjectUser, Integer> {
  @EntityGraph(attributePaths = {"roles", "user"})
  List<ProjectUser> findByProjectId(int projectId);
  Optional<ProjectUser> findByProjectIdAndUserId(int projectId, int userId);
  void deleteByProjectIdAndUserId(int projectId, int userId);
  boolean existsByUserIdAndProjectId(int userId, int projectId);
  @Modifying
  @Transactional
  @Query("DELETE FROM ProjectUser pu WHERE pu.id = :id")
  void deleteById(@Param("id") int id);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM project_user_roles WHERE project_user_id = :id", nativeQuery = true)
  void deleteProjectUserRolesByProjectUserId(@Param("id") int id);
}
