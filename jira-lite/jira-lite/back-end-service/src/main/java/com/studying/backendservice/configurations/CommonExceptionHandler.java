package com.studying.backendservice.configurations;

import jakarta.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RequiredArgsConstructor
@RestControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Object> handleEntityNotFoundException(Exception ex, WebRequest request) {
    return ResponseEntity.status(404).body(ex.getStackTrace());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
    return ResponseEntity.status(403).body(ex.getStackTrace());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(Exception ex, WebRequest request) {
    return ResponseEntity.badRequest().body(ex.getStackTrace());
  }


}
