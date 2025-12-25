package com.afavlad.homeworkpractice.security.oauth;

import com.afavlad.homeworkpractice.security.config.AppSecurityProperties;
import com.afavlad.homeworkpractice.security.model.Role;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleResolver {

  private final AppSecurityProperties appSecurityProperties;

  public Role resolve(String email) {
    if (email == null) {
      return Role.USER;
    }

    List<String> adminEmails = appSecurityProperties.adminEmails();
    if (adminEmails == null || adminEmails.isEmpty()) {
      return Role.USER;
    }

    String normalizedEmail = email.toLowerCase();
    boolean isAdmin = adminEmails.stream()
        .map(String::toLowerCase)
        .anyMatch(normalizedEmail::equals);

    return isAdmin ? Role.ADMIN : Role.USER;
  }
}
