package com.studying.backendservice.controllers;

import com.studying.backendservice.dto.CommentDTO;
import com.studying.backendservice.services.CommentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/comments")
public class CommentController {

  private final CommentService commentService;

  @Autowired
  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @PreAuthorize("@securityService.isAdminOrEditorByTaskId(#taskId)")
  @PostMapping("/tasks/{taskId}")
  public ResponseEntity<CommentDTO> addComment(@RequestBody CommentDTO dto, @PathVariable int taskId) {
    return ResponseEntity.ok(commentService.addComment(dto, taskId));
  }

  @GetMapping("/tasks/{taskId}")
  public ResponseEntity<List<CommentDTO>> getComments(@PathVariable int taskId) {
    return ResponseEntity.ok(
        commentService.getCommentsForTask(taskId).stream().map(comment -> {
          CommentDTO dto = new CommentDTO();
          dto.setId(comment.getId());
          dto.setDescription(comment.getDescription());
          dto.setAuthorName(comment.getAuthorName() != null ? comment.getAuthorName() : "Анонім");
          return dto;
        }).toList()
    );
  }

  @PreAuthorize("@securityService.isAdminOrEditorByCommentId(#id)")
  @DeleteMapping("/task/{taskId}/{id}")
  public void deleteComment(@PathVariable int id) {
    commentService.deleteComment(id);
  }

  @PreAuthorize("@securityService.isAdminOrEditorByCommentId(#id)")
  @PutMapping("/task/{taskId}/{id}")
  public void updateComment(@PathVariable int id, @RequestBody CommentDTO comment) {
    commentService.updateComment(id, comment);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CommentDTO> getCommentById(@PathVariable int id) {
    return ResponseEntity.ok(commentService.getCommentById(id));
  }

}
