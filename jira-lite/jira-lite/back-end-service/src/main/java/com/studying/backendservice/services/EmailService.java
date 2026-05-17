package com.studying.backendservice.services;

import com.studying.backendservice.dto.CommentDTO;
import com.studying.backendservice.dto.TaskDTO;
import com.studying.backendservice.models.Comment;
import com.studying.backendservice.models.Task;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender mailSender;
  private final UserService userService;

  @Autowired
  public EmailService(JavaMailSender mailSender, UserService userService) {
    this.mailSender = mailSender;
    this.userService = userService;
  }

  @Async
  public void sendTaskCreatedNotification(TaskDTO task) {
    String to = userService.getUserById(task.getInitiatorId()).getEmail();
    String subject = "Ваша задача створена";
    String link = "http://localhost:4200/task/" + task.getId();
    String html = "<h3>Ваша задача створена</h3>" +
        "<p>Назва: <b>" + task.getSummary() + "</b></p>" +
        "<p>Опис: " + task.getDescription() + "</p>" +
        "<p><a href=\"" + link + "\">Відкрити задачу</a></p>";

    sendHtmlEmail(to, subject, html);
  }

  @Async
  public void sendNewCommentNotification(TaskDTO task, CommentDTO comment) {
    String to = userService.getUserById(task.getInitiatorId()).getEmail();
    String subject = "Новий коментар до задачі";
    String link = "http://localhost:4200/task/" + task.getId();
    String html = "<h3>Новий коментар до вашої задачі</h3>" +
        "<p><b>" + comment.getAuthorName() + "</b> написав:</p>" +
        "<blockquote>" + comment.getDescription() + "</blockquote>" +
        "<p><a href=\"" + link + "\">Переглянути задачу</a></p>";

    sendHtmlEmail(to, subject, html);
  }

  @Async
  public void sendHtmlEmail(String to, String subject, String htmlContent) {
    MimeMessage message = mailSender.createMimeMessage();

    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true); // true = HTML

      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Не вдалося надіслати лист", e);
    }
  }
}

