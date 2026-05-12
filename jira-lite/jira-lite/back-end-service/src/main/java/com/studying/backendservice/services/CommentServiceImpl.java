package com.studying.backendservice.services;

import com.studying.backendservice.dto.CommentDTO;
import com.studying.backendservice.models.Comment;
import com.studying.backendservice.repositories.CommentRepository;
import com.studying.backendservice.repositories.TaskRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final TaskRepository taskRepository;
  private final EmailService emailService;

  @Autowired
  public CommentServiceImpl(CommentRepository commentRepository, TaskRepository taskRepository, EmailService emailService) {
    this.commentRepository = commentRepository;
    this.taskRepository = taskRepository;
    this.emailService = emailService;
  }

  @Transactional
  @Override
  public Comment addComment(Comment comment) {
    emailService.sendNewCommentNotification(comment.getTask(), comment);
    return commentRepository.save(comment);
  }

  @Override
  public List<Comment> getCommentsForTask(int taskId) {
    return commentRepository.findByTaskId(taskId);
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
  public Comment getCommentById(int id) {
    return commentRepository.findById(id).orElseThrow();
  }
}
