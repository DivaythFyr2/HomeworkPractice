package com.afavlad.homeworkpractice.security.controller;

import com.afavlad.homeworkpractice.security.dto.response.AdminPingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  @GetMapping("/ping")
  public AdminPingResponse ping() {
    return AdminPingResponse.builder()
        .status("ok")
        .scope("admin")
        .build();
  }

}
