package com.studying.backendservice.repositories.tenantrepos;

import com.studying.backendservice.entities.tenantentities.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task,Integer> {

  @EntityGraph(attributePaths = {"assignee", "initiator", "project"})
  List<Task> findByAssigneeId(int id);
  @EntityGraph(attributePaths = {"assignee", "initiator", "project"})
  Optional<Task> findById(int id);
  @EntityGraph(attributePaths = {"assignee", "initiator", "project"})
  List<Task> findByInitiatorId(int id);

}
