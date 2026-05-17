package com.studying.backendservice.services;

import com.studying.backendservice.dto.CommentDTO;
import com.studying.backendservice.models.Comment;
import java.util.List;

public interface CommentService {
  CommentDTO addComment(CommentDTO comment, int taskId);
  List<CommentDTO> getCommentsForTask(int taskId);
  void deleteComment(int id);
  void updateComment(int id, CommentDTO updatedComment);
  CommentDTO getCommentById(int id);
  CommentDTO toDto(Comment comment);

}
