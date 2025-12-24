package com.afavlad.homeworkpractice.security.service;

import com.afavlad.homeworkpractice.security.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

  private static final int MAX_ATTEMPTS = 5;

  private final AppUserRepository appUserRepository;

  @Transactional
  public void onSuccess(String username) {
    appUserRepository.findByUsername(username).ifPresent(u -> {
      if (u.getFailedAttempts() != 0) {
        u.setFailedAttempts(0);
        appUserRepository.save(u);
      }
    });
  }

  @Transactional
  public void onFailure(String username) {
    appUserRepository.findByUsername(username).ifPresent(u -> {
      int attempts = u.getFailedAttempts() + 1;
      u.setFailedAttempts(attempts);

      if (attempts >= MAX_ATTEMPTS) {
        u.setAccountNonLocked(false);
      }
      appUserRepository.save(u);
    });
  }

  @Transactional
  public void unlock(String username) {
    appUserRepository.findByUsername(username).ifPresent(u -> {
      u.setAccountNonLocked(true);
      u.setFailedAttempts(0);
      appUserRepository.save(u);
    });
  }
}
