package com.afavlad.homeworkpractice.controller;

import static org.mockito.ArgumentMatchers.any;
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
import com.afavlad.homeworkpractice.dto.response.OrderDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.UserDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.UserSummaryResponse;
import com.afavlad.homeworkpractice.enums.OrderStatus;
import com.afavlad.homeworkpractice.exception.ConflictException;
import com.afavlad.homeworkpractice.exception.NotFoundException;
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

  private final String GET_ALL = "/api/v1/users";
  private final String GET_WITH_ORDERS = "/api/v1/users/{id}";
  private final String POST_CREATE = "/api/v1/users";
  private final String PATCH_UPDATE = "/api/v1/users/{id}";
  private final String DELETE_USER = "/api/v1/users/{id}";

  private UUID userId() {
    return UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
  }

  private OffsetDateTime createdAt() {
    return OffsetDateTime.parse("2025-01-01T10:00:00+00:00");
  }

  @Test
  @DisplayName("GET /api/v1/users -> 200 и список summary")
  void getAll_ShouldReturnUserSummaryList() throws Exception {
    UUID id = userId();

    UserSummaryResponse u1 = UserSummaryResponse.builder()
        .id(id)
        .name("Ivan")
        .email("ivan@mail.com")
        .address("Addr")
        .createdAt(createdAt())
        .build();

    when(userService.getAll()).thenReturn(List.of(u1));

    mockMvc.perform(get(GET_ALL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(id.toString()))
        .andExpect(jsonPath("$[0].name").value("Ivan"))
        .andExpect(jsonPath("$[0].email").value("ivan@mail.com"))
        .andExpect(jsonPath("$[0].address").value("Addr"));
  }

  @Test
  @DisplayName("GET /api/v1/users/{id} -> 200 и details с orders")
  void getWithOrders_ShouldReturnUserDetails() throws Exception {
    UUID id = userId();

    OrderDetailsResponse order = OrderDetailsResponse.builder()
        .id(UUID.fromString("11111111-2222-3333-4444-555555555555"))
        .userId(id)
        .totalAmount(new BigDecimal("35.00"))
        .status(OrderStatus.NEW)
        .createdAt(createdAt())
        .items(List.of())
        .build();

    UserDetailsResponse details = UserDetailsResponse.builder()
        .id(id)
        .name("Ivan")
        .email("ivan@mail.com")
        .address("Addr")
        .createdAt(createdAt())
        .orders(List.of(order))
        .build();

    when(userService.getByIdWithOrders(id)).thenReturn(details);

    mockMvc.perform(get(GET_WITH_ORDERS, id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Ivan"))
        .andExpect(jsonPath("$.email").value("ivan@mail.com"))
        .andExpect(jsonPath("$.address").value("Addr"))
        .andExpect(jsonPath("$.orders[0].userId").value(id.toString()))
        .andExpect(jsonPath("$.orders[0].totalAmount").value(35.00))
        .andExpect(jsonPath("$.orders[0].status").value("NEW"));
  }

  @Test
  @DisplayName("POST /api/v1/users -> 201 и summary")
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
        .andExpect(jsonPath("$.name").value("Ivan"))
        .andExpect(jsonPath("$.email").value("ivan@mail.com"))
        .andExpect(jsonPath("$.address").value("Addr"));
  }

  @Test
  @DisplayName("PATCH /api/v1/users/{id} -> 200 и summary")
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

    when(userService.update(any(UUID.class), any(UpdateUserRequest.class))).thenReturn(resp);

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
  @DisplayName("POST: невалидный email -> 400 + ErrorResponseDto")
  void create_InvalidEmail_ShouldReturnBadRequest() throws Exception {
    CreateUserRequest req = new CreateUserRequest("Ivan", "not-an-email", "Addr");

    mockMvc.perform(post(POST_CREATE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.path").value("/api/v1/users"))
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @DisplayName("POST: невалидный JSON -> 400 + ErrorResponseDto")
  void create_InvalidJson_ShouldReturnBadRequest() throws Exception {
    String invalidJson = "{\"name\": \"Ivan\", \"email\": {\"oops\": true}}";

    mockMvc.perform(post(POST_CREATE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.path").value("/api/v1/users"))
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @DisplayName("GET: пользователь не найден -> 404 + ErrorResponseDto")
  void getWithOrders_UserNotFound_ShouldReturnNotFound() throws Exception {
    UUID id = userId();
    when(userService.getByIdWithOrders(id)).thenThrow(
        new NotFoundException("User not found: " + id));

    mockMvc.perform(get(GET_WITH_ORDERS, id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.path").value("/api/v1/users/" + id))
        .andExpect(jsonPath("$.message").value("User not found: " + id));
  }

  @Test
  @DisplayName("POST: конфликт email -> 409 + ErrorResponseDto")
  void create_Conflict_ShouldReturnConflict() throws Exception {
    CreateUserRequest req = new CreateUserRequest("Ivan", "ivan@mail.com", "Addr");
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
}