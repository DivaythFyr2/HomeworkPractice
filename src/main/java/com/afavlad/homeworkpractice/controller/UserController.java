package com.afavlad.homeworkpractice.controller;

import com.afavlad.homeworkpractice.dto.request.CreateUserRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateUserRequest;
import com.afavlad.homeworkpractice.dto.response.UserDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.UserSummaryResponse;
import com.afavlad.homeworkpractice.service.UserService;
import com.afavlad.homeworkpractice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  @JsonView(Views.UserSummary.class)
  public List<UserSummaryResponse> getAll() {
    return userService.getAll();
  }

  @GetMapping("/{id}")
  @JsonView(Views.UserDetails.class)
  public UserDetailsResponse getWithOrders(@PathVariable UUID id) {
    return userService.getByIdWithOrders(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @JsonView(Views.UserSummary.class)
  public UserSummaryResponse create(@RequestBody @Valid CreateUserRequest dto) {
    return userService.create(dto);
  }

  @PatchMapping("/{id}")
  @JsonView(Views.UserSummary.class)
  public UserSummaryResponse update(
      @PathVariable UUID id,
      @RequestBody @Valid UpdateUserRequest dto
  ) {
    return userService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    userService.delete(id);
  }
}