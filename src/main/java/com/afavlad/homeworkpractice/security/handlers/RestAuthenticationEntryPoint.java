package com.afavlad.homeworkpractice.security.handlers;

import com.afavlad.homeworkpractice.security.api.error.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    ErrorResponseDto body = ErrorResponseDto.builder()
        .message("UNAUTHORIZED")
        .status(401)
        .path(request.getRequestURI())
        .timestamp(Instant.now())
        .build();

    objectMapper.writeValue(response.getOutputStream(), body);
  }
}
