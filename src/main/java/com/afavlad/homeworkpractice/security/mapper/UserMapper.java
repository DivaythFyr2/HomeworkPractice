package com.afavlad.homeworkpractice.security.mapper;

import com.afavlad.homeworkpractice.security.dto.response.UserProfileResponse;
import com.afavlad.homeworkpractice.security.model.AppUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserProfileResponse toProfile(AppUser user) {
    return UserProfileResponse.builder()
        .userId(user.getId())
        .provider(user.getProvider())
        .providerUserId(user.getProviderUserId())
        .name(user.getName())
        .email(user.getEmail())
        .role(user.getRole())
        .build();
  }

}
