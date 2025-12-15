package com.afavlad.homeworkpractice.controller;

import com.afavlad.homeworkpractice.dto.request.CreateUserRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateUserRequest;
import com.afavlad.homeworkpractice.dto.response.OrderSummaryResponse;
import com.afavlad.homeworkpractice.dto.response.PageResponse;
import com.afavlad.homeworkpractice.dto.response.UserSummaryResponse;
import com.afavlad.homeworkpractice.service.OrderService;
import com.afavlad.homeworkpractice.service.UserService;
import com.afavlad.homeworkpractice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
  private final OrderService orderService;

  @GetMapping("/all")
  @JsonView(Views.UserSummary.class)
  public PageResponse<UserSummaryResponse> getAll(Pageable pageable) {
    return userService.getAll(pageable);
  }

  @GetMapping("/{id}")
  @JsonView(Views.UserDetails.class)
  public UserSummaryResponse getUserById(@PathVariable UUID id) {
    return userService.getById(id);
  }

  @GetMapping("/{id}/orders")
  @JsonView(Views.OrderSummary.class)
  public PageResponse<OrderSummaryResponse> getUserOrders(@PathVariable UUID id,
      Pageable pageable
  ) {
    return orderService.getAllByUserId(id, pageable);
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