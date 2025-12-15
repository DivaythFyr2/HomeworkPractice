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
import com.afavlad.homeworkpractice.dto.request.CreateUserRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateUserRequest;
import com.afavlad.homeworkpractice.dto.response.OrderSummaryResponse;
import com.afavlad.homeworkpractice.dto.response.PageResponse;
import com.afavlad.homeworkpractice.dto.response.UserSummaryResponse;
import com.afavlad.homeworkpractice.enums.OrderStatus;
import com.afavlad.homeworkpractice.exception.ConflictException;
import com.afavlad.homeworkpractice.exception.NotFoundException;
import com.afavlad.homeworkpractice.service.OrderService;
import com.afavlad.homeworkpractice.service.UserService;
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

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private OrderService orderService;

  private final String GET_ALL = "/api/v1/users/all";
  private final String GET_WITH_ORDERS = "/api/v1/users/{id}";
  private final String GET_USER_WITH_ORDERS = "/api/v1/users/{id}/orders";
  private final String POST_CREATE = "/api/v1/users";
  private final String PATCH_UPDATE = "/api/v1/users/{id}";
  private final String DELETE_USER = "/api/v1/users/{id}";

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
  @DisplayName("GET /api/v1/users/all?page.. -> 200 PageResponse<UserSummaryResponse>")
  void getAll_ShouldReturnUsersPage() throws Exception {
    UUID id = userId();

    UserSummaryResponse u = UserSummaryResponse.builder()
        .id(id)
        .name("Ivan")
        .email("ivan@mail.com")
        .address("Addr")
        .createdAt(createdAt())
        .build();

    PageResponse<UserSummaryResponse> page = PageResponse.<UserSummaryResponse>builder()
        .content(List.of(u))
        .page(0)
        .size(1)
        .totalElements(1)
        .totalPages(1)
        .last(true)
        .sort("createdAt,desc")
        .build();

    when(userService.getAll(any())).thenReturn(page);

    mockMvc.perform(get(GET_ALL)
            .param("page", "0")
            .param("size", "1")
            .param("sort", "createdAt,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.sort").value("createdAt,desc"))
        .andExpect(jsonPath("$.content[0].id").value(id.toString()))
        .andExpect(jsonPath("$.content[0].name").value("Ivan"))
        .andExpect(jsonPath("$.content[0].email").value("ivan@mail.com"))
        .andExpect(jsonPath("$.content[0].address").value("Addr"));
  }

  @Test
  @DisplayName("GET /api/v1/users/{id} -> 200 UserSummaryResponse")
  void getUserById_ShouldReturnUserSummary() throws Exception {
    UUID id = userId();

    UserSummaryResponse u = UserSummaryResponse.builder()
        .id(id)
        .name("Ivan")
        .email("ivan@mail.com")
        .address("Addr")
        .createdAt(createdAt())
        .build();

    when(userService.getById(id)).thenReturn(u);

    mockMvc.perform(get(GET_WITH_ORDERS, id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Ivan"))
        .andExpect(jsonPath("$.email").value("ivan@mail.com"))
        .andExpect(jsonPath("$.address").value("Addr"));
  }

  @Test
  @DisplayName("GET /api/v1/users/{id}/orders?page.. -> 200 PageResponse<OrderSummaryResponse>")
  void getUserOrders_ShouldReturnOrdersPage() throws Exception {
    UUID id = userId();

    OrderSummaryResponse o = OrderSummaryResponse.builder()
        .id(orderId())
        .userId(id)
        .totalAmount(new BigDecimal("35.00"))
        .status(OrderStatus.NEW)
        .createdAt(createdAt())
        .build();

    PageResponse<OrderSummaryResponse> page = PageResponse.<OrderSummaryResponse>builder()
        .content(List.of(o))
        .page(0)
        .size(10)
        .totalElements(1)
        .totalPages(1)
        .last(true)
        .sort("")
        .build();

    when(orderService.getAllByUserId(eq(id), any())).thenReturn(page);

    mockMvc.perform(get(GET_USER_WITH_ORDERS, id)
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(orderId().toString()))
        .andExpect(jsonPath("$.content[0].userId").value(id.toString()))
        .andExpect(jsonPath("$.content[0].status").value("NEW"));
  }

  @Test
  @DisplayName("POST /api/v1/users -> 201 UserSummaryResponse")
  void create_ShouldReturnCreatedUser() throws Exception {
    UUID id = userId();

    CreateUserRequest req = new CreateUserRequest("Ivan", "ivan@mail.com", "Addr");

    UserSummaryResponse resp = UserSummaryResponse.builder()
        .id(id)
        .name("Ivan")
        .email("ivan@mail.com")
        .address("Addr")
        .createdAt(createdAt())
        .build();

    when(userService.create(any(CreateUserRequest.class))).thenReturn(resp);

    mockMvc.perform(post(POST_CREATE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Ivan"));
  }

  @Test
  @DisplayName("POST: конфликт email -> 409")
  void create_Conflict_ShouldReturnConflict() throws Exception {
    CreateUserRequest req = new CreateUserRequest("Ivan", "dup@mail.com", "Addr");
    when(userService.create(any(CreateUserRequest.class)))
        .thenThrow(new ConflictException("Email already exists: " + req.email()));

    mockMvc.perform(post(POST_CREATE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(jsonPath("$.path").value("/api/v1/users"))
        .andExpect(jsonPath("$.message").value("Email already exists: " + req.email()));
  }

  @Test
  @DisplayName("PATCH /api/v1/users/{id} -> 200 UserSummaryResponse")
  void update_ShouldReturnUpdatedUser() throws Exception {
    UUID id = userId();
    UpdateUserRequest req = new UpdateUserRequest("NewName", null, "NewAddr");

    UserSummaryResponse resp = UserSummaryResponse.builder()
        .id(id)
        .name("NewName")
        .email("ivan@mail.com")
        .address("NewAddr")
        .createdAt(createdAt())
        .build();

    when(userService.update(eq(id), any(UpdateUserRequest.class))).thenReturn(resp);

    mockMvc.perform(patch(PATCH_UPDATE, id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("NewName"))
        .andExpect(jsonPath("$.address").value("NewAddr"));
  }

  @Test
  @DisplayName("DELETE /api/v1/users/{id} -> 204")
  void delete_ShouldReturnNoContent() throws Exception {
    UUID id = userId();

    mockMvc.perform(delete(DELETE_USER, id))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("GET /api/v1/users/{id}: not found -> 404")
  void getUserById_NotFound_ShouldReturn404() throws Exception {
    UUID id = userId();
    when(userService.getById(id)).thenThrow(new NotFoundException("User not found: " + id));

    mockMvc.perform(get(GET_WITH_ORDERS, id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.path").value("/api/v1/users/" + id))
        .andExpect(jsonPath("$.message").value("User not found: " + id));
  }

  @Test
  @DisplayName("GET /api/v1/users/{id}/orders: user not found -> 404")
  void getUserOrders_UserNotFound_ShouldReturn404() throws Exception {
    UUID id = userId();
    when(orderService.getAllByUserId(eq(id), any()))
        .thenThrow(new NotFoundException("User not found: " + id));

    mockMvc.perform(get(GET_USER_WITH_ORDERS, id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("User not found: " + id));
  }

  @Test
  @DisplayName("POST: malformed JSON -> 400 (Malformed JSON request)")
  void create_MalformedJson_ShouldReturn400() throws Exception {
    String invalidJson = "{\"name\": \"Ivan\", \"email\": {\"oops\": true}}";

    mockMvc.perform(post(POST_CREATE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Malformed JSON request"));
  }
}