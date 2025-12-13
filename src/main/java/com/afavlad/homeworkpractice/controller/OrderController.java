package com.afavlad.homeworkpractice.controller;

import com.afavlad.homeworkpractice.dto.request.CreateOrderRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateOrderStatusRequest;
import com.afavlad.homeworkpractice.dto.response.OrderDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.OrderSummaryResponse;
import com.afavlad.homeworkpractice.service.OrderService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @GetMapping
  @JsonView(Views.OrderSummary.class)
  public List<OrderSummaryResponse> getUserOrders(@RequestParam UUID userId) {
    return orderService.getAllByUserId(userId);
  }

  @GetMapping("/{orderId}")
  @JsonView(Views.OrderDetails.class)
  public OrderDetailsResponse getOrder(@PathVariable UUID orderId) {
    return orderService.getById(orderId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @JsonView(Views.OrderDetails.class)
  public OrderDetailsResponse create(@RequestBody @Valid CreateOrderRequest dto) {
    return orderService.create(dto);
  }

  @PatchMapping("/{orderId}")
  @JsonView(Views.OrderDetails.class)
  public OrderDetailsResponse changeStatus(@PathVariable UUID orderId,
      @RequestBody @Valid UpdateOrderStatusRequest dto) {
    return orderService.update(orderId, dto);
  }

  @DeleteMapping("/{orderId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID orderId) {
    orderService.delete(orderId);
  }
}
