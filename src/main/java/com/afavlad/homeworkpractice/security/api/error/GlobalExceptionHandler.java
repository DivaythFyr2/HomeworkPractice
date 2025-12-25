package com.afavlad.homeworkpractice.security.api.error;

import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponseDto> handleIllegalState(IllegalStateException ex,
      jakarta.servlet.http.HttpServletRequest request) {
    ErrorResponseDto body = ErrorResponseDto.builder()
        .message(ex.getMessage())
        .status(500)
        .path(request.getRequestURI())
        .timestamp(Instant.now())
        .build();

    return ResponseEntity.status(500).body(body);
  }

}
