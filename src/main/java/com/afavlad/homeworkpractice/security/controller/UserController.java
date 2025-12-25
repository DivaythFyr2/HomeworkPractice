package com.afavlad.homeworkpractice.security.controller;

import com.afavlad.homeworkpractice.security.dto.response.UserProfileResponse;
import com.afavlad.homeworkpractice.security.mapper.UserMapper;
import com.afavlad.homeworkpractice.security.model.AppUser;
import com.afavlad.homeworkpractice.security.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

  private final AppUserService appUserService;
  private final UserMapper userMapper;

  @GetMapping("/me")
  public UserProfileResponse me(OAuth2AuthenticationToken authentication) {
    AppUser user = appUserService.getCurrentUser(authentication);
    return userMapper.toProfile(user);
  }

}
