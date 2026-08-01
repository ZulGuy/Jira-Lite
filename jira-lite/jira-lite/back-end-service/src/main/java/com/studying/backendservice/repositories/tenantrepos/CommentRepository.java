package com.studying.backendservice.repositories.tenantrepos;

import com.studying.backendservice.entities.tenantentities.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
  @EntityGraph(attributePaths = {"author", "task"})
  List<Comment> findByTaskId(int id);
  @EntityGraph(attributePaths = {"author", "task"})
  void deleteById(int id);
  @EntityGraph(attributePaths = {"author", "task"})
  void deleteAllByTaskId(int id);

}
