package com.afavlad.homeworkpractice.security.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationEventsListener {

  @EventListener
  public void onSuccess(AuthenticationSuccessEvent event) {
    String username = event.getAuthentication().getName();
    log.info("AUTH success user={}", username);
  }

  @EventListener
  public void onBadCredentials(AuthenticationFailureBadCredentialsEvent event) {
    String username = event.getAuthentication().getName();
    Exception ex = event.getException();
    log.warn("AUTH failure bad_credentials user={} ex={}", username, ex.getClass().getSimpleName());
  }

  @EventListener
  public void onLocked(AuthenticationFailureLockedEvent event) {
    String username = event.getAuthentication().getName();
    Exception ex = event.getException();
    log.warn("AUTH failure locked user={} ex={}", username, ex.getClass().getSimpleName());
  }
}
