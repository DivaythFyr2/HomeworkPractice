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
import com.afavlad.homeworkpractice.dto.request.CreateOrderItemRequest;
import com.afavlad.homeworkpractice.dto.request.CreateOrderRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateOrderStatusRequest;
import com.afavlad.homeworkpractice.dto.response.OrderDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.OrderItemResponse;
import com.afavlad.homeworkpractice.dto.response.OrderSummaryResponse;
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

  private final String GET_USER_ORDERS = "/api/v1/orders";
  private final String GET_ORDER = "/api/v1/orders/{orderId}";
  private final String POST_CREATE = "/api/v1/orders";
  private final String PATCH_STATUS = "/api/v1/orders/{orderId}";
  private final String DELETE_ORDER = "/api/v1/orders/{orderId}";

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
  @DisplayName("GET /api/v1/orders?userId=... -> 200 и список summary")
  void getUserOrders_ShouldReturnOrderSummaries() throws Exception {
    UUID uid = userId();

    OrderSummaryResponse summary = OrderSummaryResponse.builder()
        .id(orderId())
        .userId(uid)
        .totalAmount(new BigDecimal("35.00"))
        .status(OrderStatus.NEW)
        .createdAt(createdAt())
        .build();

    when(orderService.getAllByUserId(uid)).thenReturn(List.of(summary));

    mockMvc.perform(get(GET_USER_ORDERS).param("userId", uid.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(summary.id().toString()))
        .andExpect(jsonPath("$[0].userId").value(uid.toString()))
        .andExpect(jsonPath("$[0].totalAmount").value(35.00))
        .andExpect(jsonPath("$[0].status").value("NEW"));
  }

  @Test
  @DisplayName("GET /api/v1/orders/{orderId} -> 200 и details")
  void getOrder_ShouldReturnOrderDetails() throws Exception {
    UUID oid = orderId();
    UUID uid = userId();

    OrderItemResponse item = OrderItemResponse.builder()
        .id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"))
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
        .andExpect(jsonPath("$.totalAmount").value(20.00))
        .andExpect(jsonPath("$.status").value("NEW"))
        .andExpect(jsonPath("$.items[0].sku").value("SKU1"))
        .andExpect(jsonPath("$.items[0].productName").value("Apple"))
        .andExpect(jsonPath("$.items[0].quantity").value(2))
        .andExpect(jsonPath("$.items[0].unitPrice").value(10.00));
  }

  @Test
  @DisplayName("POST /api/v1/orders -> 201 и details")
  void create_ShouldReturnCreatedOrder() throws Exception {
    UUID uid = userId();
    UUID oid = orderId();

    CreateOrderRequest req = new CreateOrderRequest(uid, List.of(
        new CreateOrderItemRequest("SKU1", "Apple", 2, new BigDecimal("10.00"))
    ));

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
        .andExpect(jsonPath("$.userId").value(uid.toString()))
        .andExpect(jsonPath("$.totalAmount").value(20.00))
        .andExpect(jsonPath("$.status").value("NEW"));
  }

  @Test
  @DisplayName("PATCH /api/v1/orders/{orderId} -> 200 и details с новым статусом")
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

  @Test
  @DisplayName("GET user orders: user not found -> 404 + ErrorResponseDto")
  void getUserOrders_UserNotFound_ShouldReturnNotFound() throws Exception {
    UUID uid = userId();

    when(orderService.getAllByUserId(uid))
        .thenThrow(new NotFoundException("User not found: " + uid));

    mockMvc.perform(get(GET_USER_ORDERS).param("userId", uid.toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.path").value("/api/v1/orders"))
        .andExpect(jsonPath("$.message").value("User not found: " + uid));
  }

  @Test
  @DisplayName("GET order: order not found -> 404 + ErrorResponseDto")
  void getOrder_OrderNotFound_ShouldReturnNotFound() throws Exception {
    UUID oid = orderId();
    when(orderService.getById(oid)).thenThrow(new NotFoundException("Order not found: " + oid));

    mockMvc.perform(get(GET_ORDER, oid))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.path").value("/api/v1/orders/" + oid))
        .andExpect(jsonPath("$.message").value("Order not found: " + oid));
  }

  @Test
  @DisplayName("POST: validation error (items пустой) -> 400 + ErrorResponseDto")
  void create_ItemsEmpty_ShouldReturnBadRequest() throws Exception {
    UUID uid = userId();
    CreateOrderRequest req = new CreateOrderRequest(uid, List.of());

    mockMvc.perform(post(POST_CREATE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.path").value("/api/v1/orders"))
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @DisplayName("POST: malformed JSON -> 400 + Malformed JSON request")
  void create_MalformedJson_ShouldReturnBadRequest() throws Exception {
    String invalidJson = "{\"userId\":\"" + userId() + "\",\"items\":{}}";

    mockMvc.perform(post(POST_CREATE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.path").value("/api/v1/orders"))
        .andExpect(jsonPath("$.message").value("Malformed JSON request"));
  }

  @Test
  @DisplayName("PATCH: validation error (status = null) -> 400")
  void changeStatus_StatusNull_ShouldReturnBadRequest() throws Exception {
    UUID oid = orderId();
    String json = "{\"status\": null}";

    mockMvc.perform(patch(PATCH_STATUS, oid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.path").value("/api/v1/orders/" + oid))
        .andExpect(jsonPath("$.message").exists());
  }
}