package com.afavlad.homeworkpractice.security.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    long start = System.currentTimeMillis();
    String method = request.getMethod();
    String uri = request.getRequestURI();

    try {
      filterChain.doFilter(request, response);
    } finally {
      long ms = System.currentTimeMillis() - start;
      log.info("REQ {} {} -> {} ({} ms)", method, uri, response.getStatus(), ms);
    }
  }
}