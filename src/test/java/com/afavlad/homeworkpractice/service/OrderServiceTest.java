package com.afavlad.homeworkpractice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.afavlad.homeworkpractice.dto.request.CreateOrderItemRequest;
import com.afavlad.homeworkpractice.dto.request.CreateOrderRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateOrderStatusRequest;
import com.afavlad.homeworkpractice.dto.response.OrderDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.OrderSummaryResponse;
import com.afavlad.homeworkpractice.entity.Order;
import com.afavlad.homeworkpractice.entity.OrderItem;
import com.afavlad.homeworkpractice.entity.User;
import com.afavlad.homeworkpractice.enums.OrderStatus;
import com.afavlad.homeworkpractice.exception.NotFoundException;
import com.afavlad.homeworkpractice.mapper.OrderMapper;
import com.afavlad.homeworkpractice.repository.OrderRepository;
import com.afavlad.homeworkpractice.repository.UserRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private OrderMapper orderMapper;

  @InjectMocks
  private OrderService orderService;

  private UUID userId() {
    return UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
  }

  private UUID orderId() {
    return UUID.fromString("11111111-2222-3333-4444-555555555555");
  }

  private OffsetDateTime createdAt() {
    return OffsetDateTime.parse("2025-01-01T10:00:00+00:00");
  }

  private User user(UUID id) {
    return User.builder()
        .id(id)
        .name("Ivan")
        .email("ivan@mail.com")
        .address("Addr")
        .createdAt(createdAt())
        .build();
  }

  @Test
  @DisplayName("пользователь существует -> список заказов")
  void getAllByUserId_WhenUserExists_ShouldReturnSummaries() {
    UUID uid = userId();
    User user = user(uid);

    Order o1 = Order.builder()
        .id(UUID.randomUUID())
        .user(user)
        .status(OrderStatus.NEW)
        .totalAmount(BigDecimal.valueOf(100))
        .createdAt(createdAt())
        .build();

    OrderSummaryResponse r1 = OrderSummaryResponse.builder()
        .id(o1.getId())
        .userId(uid)
        .totalAmount(o1.getTotalAmount())
        .status(o1.getStatus())
        .createdAt(o1.getCreatedAt())
        .build();

    when(userRepository.existsById(uid)).thenReturn(true);
    when(orderRepository.findAllByUserId(uid)).thenReturn(List.of(o1));
    when(orderMapper.toSummary(o1)).thenReturn(r1);

    List<OrderSummaryResponse> result = orderService.getAllByUserId(uid);

    assertEquals(1, result.size());
    assertEquals(r1, result.get(0));

    verify(userRepository).existsById(uid);
    verify(orderRepository).findAllByUserId(uid);
    verify(orderMapper).toSummary(o1);
  }

  @Test
  @DisplayName("пользователь не найден -> NotFoundException")
  void getAllByUserId_WhenUserNotFound_ShouldThrow() {
    UUID uid = userId();
    when(userRepository.existsById(uid)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> orderService.getAllByUserId(uid));

    verify(userRepository).existsById(uid);
    verifyNoInteractions(orderRepository, orderMapper);
  }

  @Test
  @DisplayName("заказ найден -> details")
  void getById_WhenOrderExists_ShouldReturnDetails() {
    UUID oid = orderId();
    User user = user(userId());

    Order order = Order.builder()
        .id(oid)
        .user(user)
        .status(OrderStatus.NEW)
        .totalAmount(BigDecimal.TEN)
        .createdAt(createdAt())
        .build();

    OrderDetailsResponse expected = OrderDetailsResponse.builder()
        .id(order.getId())
        .userId(user.getId())
        .totalAmount(order.getTotalAmount())
        .status(order.getStatus())
        .createdAt(order.getCreatedAt())
        .items(List.of())
        .build();

    when(orderRepository.findWithItemsById(oid)).thenReturn(Optional.of(order));
    when(orderMapper.toDetails(order)).thenReturn(expected);

    OrderDetailsResponse result = orderService.getById(oid);

    assertEquals(expected, result);
    verify(orderRepository).findWithItemsById(oid);
    verify(orderMapper).toDetails(order);
  }

  @Test
  @DisplayName("успешное создание заказа с подсчетом суммы")
  void create_WhenValid_ShouldCreateOrderWithItemsAndTotal() {
    UUID uid = userId();
    User user = user(uid);

    CreateOrderRequest dto = new CreateOrderRequest(
        uid,
        List.of(
            new CreateOrderItemRequest("SKU1", "Apple", 2, new BigDecimal("10.00")),
            new CreateOrderItemRequest("SKU2", "Banana", 3, new BigDecimal("5.00"))
        )
    );

    when(userRepository.findById(uid)).thenReturn(Optional.of(user));

    Order saved = Order.builder()
        .id(orderId())
        .user(user)
        .status(OrderStatus.NEW)
        .build();

    when(orderRepository.save(any(Order.class))).thenReturn(saved);
    when(orderRepository.findWithItemsById(saved.getId()))
        .thenReturn(Optional.of(saved));

    OrderDetailsResponse response = OrderDetailsResponse.builder()
        .id(saved.getId())
        .userId(uid)
        .totalAmount(new BigDecimal("35.00"))
        .status(OrderStatus.NEW)
        .items(List.of())
        .build();

    when(orderMapper.toDetails(saved)).thenReturn(response);

    OrderDetailsResponse result = orderService.create(dto);

    assertEquals(new BigDecimal("35.00"), result.totalAmount());
    assertEquals(OrderStatus.NEW, result.status());

    ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepository).save(captor.capture());

    Order toSave = captor.getValue();
    assertEquals(OrderStatus.NEW, toSave.getStatus());
    assertEquals(new BigDecimal("35.00"), toSave.getTotalAmount());
    assertEquals(2, toSave.getItems().size());

    OrderItem item1 = toSave.getItems().get(0);
    assertEquals("SKU1", item1.getSku());
    assertEquals("Apple", item1.getProductName());
    assertEquals(2, item1.getQuantity());
    assertEquals(new BigDecimal("10.00"), item1.getUnitPrice());
  }

  @Test
  @DisplayName("смена статуса заказа")
  void update_WhenOrderExists_ShouldUpdateStatus() {
    UUID oid = orderId();
    User user = user(userId());

    Order order = Order.builder()
        .id(oid)
        .user(user)
        .status(OrderStatus.NEW)
        .totalAmount(BigDecimal.TEN)
        .build();

    when(orderRepository.findById(oid)).thenReturn(Optional.of(order));
    when(orderRepository.save(order)).thenReturn(order);

    OrderDetailsResponse response = OrderDetailsResponse.builder()
        .id(oid)
        .userId(user.getId())
        .status(OrderStatus.PAID)
        .totalAmount(BigDecimal.TEN)
        .build();

    when(orderMapper.toDetails(order)).thenReturn(response);

    OrderDetailsResponse result =
        orderService.update(oid, new UpdateOrderStatusRequest(OrderStatus.PAID));

    assertEquals(OrderStatus.PAID, order.getStatus());
    assertEquals(OrderStatus.PAID, result.status());
  }

  @Test
  @DisplayName("заказ не найден -> NotFoundException")
  void delete_WhenOrderNotFound_ShouldThrow() {
    UUID oid = orderId();
    when(orderRepository.existsById(oid)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> orderService.delete(oid));
  }

  @Test
  @DisplayName("заказ найден -> deleteById")
  void delete_WhenOrderExists_ShouldDelete() {
    UUID oid = orderId();
    when(orderRepository.existsById(oid)).thenReturn(true);

    orderService.delete(oid);

    verify(orderRepository).deleteById(oid);
  }
}
