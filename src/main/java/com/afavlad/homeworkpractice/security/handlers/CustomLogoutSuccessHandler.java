package com.afavlad.homeworkpractice.security.handlers;

import com.afavlad.homeworkpractice.security.dto.response.ApiMessageResponse;
import com.afavlad.homeworkpractice.security.oauth.TokenRevocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

  private final TokenRevocationService tokenRevocationService;
  private final ObjectMapper objectMapper;

  @Override
  public void onLogoutSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException {

    String principal = authentication != null ? authentication.getName() : "anonymous";
    log.info("LOGOUT_SUCCESS principal={}", principal);

    tokenRevocationService.revoke(request, authentication);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ApiMessageResponse body = ApiMessageResponse.builder()
        .message("Logout successful")
        .path(request.getRequestURI())
        .timestamp(Instant.now())
        .build();

    objectMapper.writeValue(response.getOutputStream(), body);
  }

}
