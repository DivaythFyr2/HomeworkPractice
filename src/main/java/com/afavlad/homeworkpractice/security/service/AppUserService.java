package com.afavlad.homeworkpractice.security.service;

import com.afavlad.homeworkpractice.security.model.AppUser;
import com.afavlad.homeworkpractice.security.model.AuthProvider;
import com.afavlad.homeworkpractice.security.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppUserService {

  private final AppUserRepository appUserRepository;

  @Transactional(readOnly = true)
  public AppUser getCurrentUser(OAuth2AuthenticationToken authentication) {
    String providerUserId = authentication.getName();

    return appUserRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, providerUserId)
        .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
  }

}
