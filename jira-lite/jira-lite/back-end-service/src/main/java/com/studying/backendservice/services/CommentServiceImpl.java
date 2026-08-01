package com.studying.backendservice.services;

import com.studying.backendservice.dto.CommentDTO;
import com.studying.backendservice.entities.tenantentities.Comment;
import com.studying.backendservice.entities.tenantentities.Task;
import com.studying.backendservice.entities.tenantentities.User;
import com.studying.backendservice.repositories.tenantrepos.CommentRepository;
import com.studying.backendservice.repositories.tenantrepos.TaskRepository;
import com.studying.backendservice.repositories.tenantrepos.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final TaskRepository taskRepository;
  private final TaskService taskService;
  private final UserRepository userRepository;
  private final EmailService emailService;

  @Autowired
  public CommentServiceImpl(CommentRepository commentRepository,
      TaskRepository taskRepository, EmailService emailService,
      UserRepository userRepository, TaskService taskService) {
    this.commentRepository = commentRepository;
    this.taskRepository = taskRepository;
    this.emailService = emailService;
    this.userRepository = userRepository;
    this.taskService = taskService;
  }

  @Transactional
  @Override
  public CommentDTO addComment(CommentDTO comment, int taskId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    User author = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    Task task = taskRepository.findById(taskId).orElseThrow(
        () -> new EntityNotFoundException("Task not found")
    );
    Comment newComment = new Comment(comment.getDescription(), author, task);
    emailService.sendNewCommentNotification(taskService.getTaskById(taskId), comment);
    commentRepository.save(newComment);
    return comment;
  }

  @Override
  public List<CommentDTO> getCommentsForTask(int taskId) {
    return commentRepository.findByTaskId(taskId).stream().map(this::toDto).toList();
  }

  @Override
  public void deleteComment(int id) {
    commentRepository.deleteById(id);
  }

  @Override
  @Transactional
  public void updateComment(int id, CommentDTO updatedComment) {
    Comment existing = commentRepository.findById(id).orElseThrow();
    existing.setDescription(updatedComment.getDescription());
    commentRepository.save(existing);
  }

  @Override
  public CommentDTO getCommentById(int id) {
    return commentRepository.findById(id)
        .map(this::toDto)
        .orElseThrow(() -> new EntityNotFoundException("Comment not found!"));
  }

  @Override
  public CommentDTO toDto(Comment comment) {
    CommentDTO dto = new CommentDTO();
    dto.setId(comment.getId());
    dto.setDescription(comment.getDescription());
    dto.setAuthorName(comment.getAuthor().getEmail());
    return dto;
  }
}
