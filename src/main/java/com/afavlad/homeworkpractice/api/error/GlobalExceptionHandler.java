package com.afavlad.homeworkpractice.api.error;

import com.afavlad.homeworkpractice.exception.NotFoundException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleNotFound(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponseDto.builder()
            .message(ex.getMessage())
            .status(HttpStatus.NOT_FOUND.value())
            .timestamp(Instant.now())
            .build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .findFirst()
        .orElse("Validation error");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponseDto.builder()
            .message(message)
            .status(HttpStatus.BAD_REQUEST.value())
            .timestamp(Instant.now())
            .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleAny(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponseDto.builder()
            .message("Internal server error")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .timestamp(Instant.now())
            .build());
  }
}

