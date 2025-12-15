package com.afavlad.homeworkpractice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.afavlad.homeworkpractice.api.error.GlobalExceptionHandler;
import com.afavlad.homeworkpractice.dto.request.CreateOrderRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateOrderStatusRequest;
import com.afavlad.homeworkpractice.dto.response.OrderDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.OrderItemResponse;
import com.afavlad.homeworkpractice.dto.response.OrderSummaryResponse;
import com.afavlad.homeworkpractice.dto.response.PageResponse;
import com.afavlad.homeworkpractice.enums.OrderStatus;
import com.afavlad.homeworkpractice.exception.NotFoundException;
import com.afavlad.homeworkpractice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private OrderService orderService;

  private static final String GET_USER_ORDERS = "/api/v1/orders";
  private static final String GET_ORDER = "/api/v1/orders/{orderId}";
  private static final String POST_CREATE = "/api/v1/orders";
  private static final String PATCH_STATUS = "/api/v1/orders/{orderId}";
  private static final String DELETE_ORDER = "/api/v1/orders/{orderId}";

  private UUID userId() {
    return UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
  }

  private UUID orderId() {
    return UUID.fromString("11111111-2222-3333-4444-555555555555");
  }

  private OffsetDateTime createdAt() {
    return OffsetDateTime.parse("2025-01-01T10:00:00+00:00");
  }

  @Test
  @DisplayName("GET /api/v1/orders?userId=.. -> 200 PageResponse<OrderSummaryResponse>")
  void getUserOrders_ShouldReturnOrdersPage() throws Exception {
    UUID uid = userId();

    OrderSummaryResponse order = OrderSummaryResponse.builder()
        .id(orderId())
        .userId(uid)
        .totalAmount(new BigDecimal("35.00"))
        .status(OrderStatus.NEW)
        .createdAt(createdAt())
        .build();

    PageResponse<OrderSummaryResponse> page = PageResponse.<OrderSummaryResponse>builder()
        .content(List.of(order))
        .page(0)
        .size(10)
        .totalElements(1)
        .totalPages(1)
        .last(true)
        .sort("createdAt,desc")
        .build();

    when(orderService.getAllByUserId(eq(uid), any())).thenReturn(page);

    mockMvc.perform(get(GET_USER_ORDERS)
            .param("userId", uid.toString())
            .param("page", "0")
            .param("size", "10")
            .param("sort", "createdAt,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].id").value(orderId().toString()))
        .andExpect(jsonPath("$.content[0].status").value("NEW"));
  }

  @Test
  @DisplayName("GET /api/v1/orders?userId=.. -> 404 если пользователь не найден")
  void getUserOrders_UserNotFound_ShouldReturn404() throws Exception {
    UUID uid = userId();

    when(orderService.getAllByUserId(eq(uid), any()))
        .thenThrow(new NotFoundException("User not found: " + uid));

    mockMvc.perform(get(GET_USER_ORDERS).param("userId", uid.toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("User not found: " + uid));
  }

  @Test
  @DisplayName("GET /api/v1/orders/{orderId} -> 200 OrderDetailsResponse")
  void getOrder_ShouldReturnOrderDetails() throws Exception {
    UUID oid = orderId();
    UUID uid = userId();

    OrderItemResponse item = OrderItemResponse.builder()
        .id(UUID.randomUUID())
        .sku("SKU1")
        .productName("Apple")
        .quantity(2)
        .unitPrice(new BigDecimal("10.00"))
        .build();

    OrderDetailsResponse details = OrderDetailsResponse.builder()
        .id(oid)
        .userId(uid)
        .totalAmount(new BigDecimal("20.00"))
        .status(OrderStatus.NEW)
        .createdAt(createdAt())
        .items(List.of(item))
        .build();

    when(orderService.getById(oid)).thenReturn(details);

    mockMvc.perform(get(GET_ORDER, oid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(oid.toString()))
        .andExpect(jsonPath("$.userId").value(uid.toString()))
        .andExpect(jsonPath("$.status").value("NEW"))
        .andExpect(jsonPath("$.items[0].sku").value("SKU1"));
  }

  @Test
  @DisplayName("POST /api/v1/orders -> 201 OrderDetailsResponse")
  void create_ShouldReturnCreatedOrder() throws Exception {
    UUID uid = userId();
    UUID oid = orderId();

    CreateOrderRequest req = new CreateOrderRequest(
        uid,
        List.of(new com.afavlad.homeworkpractice.dto.request.CreateOrderItemRequest(
            "SKU1", "Apple", 2, new BigDecimal("10.00")
        ))
    );

    OrderDetailsResponse resp = OrderDetailsResponse.builder()
        .id(oid)
        .userId(uid)
        .totalAmount(new BigDecimal("20.00"))
        .status(OrderStatus.NEW)
        .createdAt(createdAt())
        .items(List.of())
        .build();

    when(orderService.create(any(CreateOrderRequest.class))).thenReturn(resp);

    mockMvc.perform(post(POST_CREATE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(oid.toString()))
        .andExpect(jsonPath("$.status").value("NEW"));
  }

  @Test
  @DisplayName("PATCH /api/v1/orders/{orderId} -> 200 статус обновлён")
  void changeStatus_ShouldReturnUpdatedOrder() throws Exception {
    UUID uid = userId();
    UUID oid = orderId();

    UpdateOrderStatusRequest req = new UpdateOrderStatusRequest(OrderStatus.PAID);

    OrderDetailsResponse resp = OrderDetailsResponse.builder()
        .id(oid)
        .userId(uid)
        .totalAmount(new BigDecimal("20.00"))
        .status(OrderStatus.PAID)
        .createdAt(createdAt())
        .items(List.of())
        .build();

    when(orderService.update(eq(oid), any(UpdateOrderStatusRequest.class))).thenReturn(resp);

    mockMvc.perform(patch(PATCH_STATUS, oid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(oid.toString()))
        .andExpect(jsonPath("$.status").value("PAID"));
  }

  @Test
  @DisplayName("DELETE /api/v1/orders/{orderId} -> 204")
  void delete_ShouldReturnNoContent() throws Exception {
    UUID oid = orderId();

    mockMvc.perform(delete(DELETE_ORDER, oid))
        .andExpect(status().isNoContent());
  }
}