package com.afavlad.homeworkpractice.security.handlers;

import com.afavlad.homeworkpractice.security.api.error.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");

    ErrorResponseDto body = ErrorResponseDto.builder()
        .message("FORBIDDEN")
        .status(403)
        .path(request.getRequestURI())
        .timestamp(Instant.now())
        .build();

    objectMapper.writeValue(response.getOutputStream(), body);
  }
}
