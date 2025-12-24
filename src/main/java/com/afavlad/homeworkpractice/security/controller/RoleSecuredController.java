package com.afavlad.homeworkpractice.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RoleSecuredController {

  /**
   * Доступен любому залогиненному пользователю (USER/MODERATOR/SUPER_ADMIN).
   */
  @GetMapping("/profile")
  @PreAuthorize("isAuthenticated()")
  public String profile() {
    return "profile";
  }

  /**
   * Доступ только MODERATOR и выше.
   */
  @PostMapping("/moderate")
  @PreAuthorize("hasAnyRole('MODERATOR','SUPER_ADMIN')")
  public String moderate() {
    return "moderate ok";
  }

  /**
   * Доступ только SUPER_ADMIN.
   */
  @DeleteMapping("/admin/users/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public String deleteUser(@PathVariable String id) {
    return "deleted " + id;
  }

  @GetMapping("/home")
  public String home() {
    return "home";
  }
}
