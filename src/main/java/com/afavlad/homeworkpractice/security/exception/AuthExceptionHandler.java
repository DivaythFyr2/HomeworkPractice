package com.afavlad.homeworkpractice.security.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public AuthErrorResponseDto badCredentials(BadCredentialsException e) {
    return AuthErrorResponseDto.builder()
        .message("Invalid username/password")
        .status(HttpStatus.UNAUTHORIZED.value())
        .timestamp(Instant.now())
        .build();
  }

  @ExceptionHandler(LockedException.class)
  @ResponseStatus(HttpStatus.LOCKED)
  public AuthErrorResponseDto locked(LockedException e) {
    return AuthErrorResponseDto.builder()
        .message("Account is locked after too many failed attempts")
        .status(HttpStatus.LOCKED.value())
        .timestamp(Instant.now())
        .build();
  }

}
