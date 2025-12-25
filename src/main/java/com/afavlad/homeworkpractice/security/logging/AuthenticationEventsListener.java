package com.afavlad.homeworkpractice.security.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationEventsListener {

  @EventListener
  public void onSuccess(AuthenticationSuccessEvent event) {
    Authentication authentication = event.getAuthentication();
    log.info("AUTH_SUCCESS principal={} authorities={}", authentication.getName(), authentication.getAuthorities());
  }
}
