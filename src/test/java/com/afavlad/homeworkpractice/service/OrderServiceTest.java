package com.afavlad.homeworkpractice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.afavlad.homeworkpractice.dto.request.CreateOrderItemRequest;
import com.afavlad.homeworkpractice.dto.request.CreateOrderRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateOrderStatusRequest;
import com.afavlad.homeworkpractice.dto.response.OrderDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.OrderSummaryResponse;
import com.afavlad.homeworkpractice.dto.response.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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

  private Order order(UUID id, User user, OrderStatus status, BigDecimal total, List<OrderItem> items) {
    return Order.builder()
        .id(id)
        .user(user)
        .status(status)
        .totalAmount(total)
        .createdAt(createdAt())
        .items(items)
        .build();
  }

  private OrderItem item(Order order, String sku, String productName, int qty, BigDecimal unitPrice) {
    return OrderItem.builder()
        .id(UUID.randomUUID())
        .order(order)
        .sku(sku)
        .productName(productName)
        .quantity(qty)
        .unitPrice(unitPrice)
        .build();
  }

  private OrderSummaryResponse summary(Order o) {
    return OrderSummaryResponse.builder()
        .id(o.getId())
        .userId(o.getUser().getId())
        .totalAmount(o.getTotalAmount())
        .status(o.getStatus())
        .createdAt(o.getCreatedAt())
        .build();
  }

  @Test
  @DisplayName("пользователь существует -> PageResponse<OrderSummaryResponse>")
  void getAllByUserId_WhenUserExists_ShouldReturnPageResponse() {
    UUID uid = userId();
    PageRequest pageable = PageRequest.of(0, 2, Sort.by(Sort.Order.desc("createdAt")));

    User u = user(uid);

    Order o1 = order(UUID.randomUUID(), u, OrderStatus.NEW, new BigDecimal("10.00"), List.of());
    Order o2 = order(UUID.randomUUID(), u, OrderStatus.PAID, new BigDecimal("20.00"), List.of());

    OrderSummaryResponse r1 = summary(o1);
    OrderSummaryResponse r2 = summary(o2);

    Page<Order> ordersPage = new PageImpl<>(List.of(o1, o2), pageable, 5);

    when(userRepository.existsById(uid)).thenReturn(true);
    when(orderRepository.findAllByUserId(uid, pageable)).thenReturn(ordersPage);
    when(orderMapper.toSummary(o1)).thenReturn(r1);
    when(orderMapper.toSummary(o2)).thenReturn(r2);

    PageResponse<OrderSummaryResponse> result = orderService.getAllByUserId(uid, pageable);

    assertNotNull(result);
    assertEquals(0, result.page());
    assertEquals(2, result.size());
    assertEquals(5, result.totalElements());
    assertEquals(3, result.totalPages());
    assertFalse(result.last());
    assertEquals("createdAt,desc", result.sort());
    assertEquals(List.of(r1, r2), result.content());

    verify(userRepository, times(1)).existsById(uid);
    verify(orderRepository, times(1)).findAllByUserId(uid, pageable);
    verify(orderMapper, times(1)).toSummary(o1);
    verify(orderMapper, times(1)).toSummary(o2);
  }

  @Test
  @DisplayName("пользователь не найден -> NotFoundException")
  void getAllByUserId_WhenUserNotFound_ShouldThrow() {
    UUID uid = userId();
    PageRequest pageable = PageRequest.of(0, 10);

    when(userRepository.existsById(uid)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> orderService.getAllByUserId(uid, pageable));

    verify(userRepository, times(1)).existsById(uid);
    verifyNoInteractions(orderRepository, orderMapper);
  }

  @Test
  @DisplayName("пустая страница -> корректный PageResponse")
  void getAllByUserId_WhenEmpty_ShouldReturnEmptyPageResponse() {
    UUID uid = userId();
    PageRequest pageable = PageRequest.of(1, 10);

    when(userRepository.existsById(uid)).thenReturn(true);
    when(orderRepository.findAllByUserId(uid, pageable))
        .thenReturn(new PageImpl<>(List.of(), pageable, 0));

    PageResponse<OrderSummaryResponse> result = orderService.getAllByUserId(uid, pageable);

    assertNotNull(result);
    assertEquals(1, result.page());
    assertEquals(10, result.size());
    assertEquals(0, result.totalElements());
    assertEquals(0, result.totalPages());
    assertTrue(result.content().isEmpty());

    verify(userRepository).existsById(uid);
    verify(orderRepository).findAllByUserId(uid, pageable);
    verifyNoInteractions(orderMapper);
  }

  @Test
  @DisplayName("заказ найден -> details")
  void getById_WhenOrderExists_ShouldReturnDetails() {
    UUID oid = orderId();
    UUID uid = userId();
    User u = user(uid);

    Order o = order(oid, u, OrderStatus.NEW, new BigDecimal("35.00"), List.of());

    OrderDetailsResponse expected = mock(OrderDetailsResponse.class);

    when(orderRepository.findWithItemsById(oid)).thenReturn(Optional.of(o));
    when(orderMapper.toDetails(o)).thenReturn(expected);

    OrderDetailsResponse result = orderService.getById(oid);

    assertNotNull(result);
    assertEquals(expected, result);

    verify(orderRepository, times(1)).findWithItemsById(oid);
    verify(orderMapper, times(1)).toDetails(o);
  }

  @Test
  @DisplayName("заказ не найден -> NotFoundException")
  void getById_WhenOrderNotFound_ShouldThrow() {
    UUID oid = orderId();
    when(orderRepository.findWithItemsById(oid)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> orderService.getById(oid));

    verify(orderRepository, times(1)).findWithItemsById(oid);
    verifyNoInteractions(orderMapper);
  }

  @Test
  @DisplayName("create: пользователь не найден -> NotFoundException")
  void create_WhenUserNotFound_ShouldThrow() {
    UUID uid = userId();

    CreateOrderRequest dto = new CreateOrderRequest(uid, List.of(
        new CreateOrderItemRequest("SKU1", "Apple", 1, new BigDecimal("10.00"))
    ));

    when(userRepository.findById(uid)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> orderService.create(dto));

    verify(userRepository, times(1)).findById(uid);
    verifyNoInteractions(orderRepository, orderMapper);
  }

  @Test
  @DisplayName("OK -> статус NEW, items собраны, totalAmount посчитан, reload, details")
  void create_WhenValid_ShouldSaveOrderWithItemsAndTotal() {
    UUID uid = userId();
    User u = user(uid);

    CreateOrderItemRequest i1 = new CreateOrderItemRequest("SKU1", "Apple", 2, new BigDecimal("10.00"));
    CreateOrderItemRequest i2 = new CreateOrderItemRequest("SKU2", "Banana", 3, new BigDecimal("5.00"));
    CreateOrderRequest dto = new CreateOrderRequest(uid, List.of(i1, i2));

    when(userRepository.findById(uid)).thenReturn(Optional.of(u));

    UUID savedId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    when(orderRepository.save(any(Order.class))).thenReturn(Order.builder().id(savedId).build());

    Order savedWithItems = Order.builder()
        .id(savedId)
        .user(u)
        .status(OrderStatus.NEW)
        .totalAmount(new BigDecimal("35.00"))
        .createdAt(createdAt())
        .items(List.of())
        .build();

    when(orderRepository.findWithItemsById(savedId)).thenReturn(Optional.of(savedWithItems));

    OrderDetailsResponse expected = mock(OrderDetailsResponse.class);
    when(orderMapper.toDetails(savedWithItems)).thenReturn(expected);

    OrderDetailsResponse result = orderService.create(dto);

    assertNotNull(result);
    assertEquals(expected, result);

    ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepository).save(orderCaptor.capture());
    Order toSave = orderCaptor.getValue();

    assertEquals(u, toSave.getUser());
    assertEquals(OrderStatus.NEW, toSave.getStatus());
    assertEquals(new BigDecimal("35.00"), toSave.getTotalAmount());

    assertNotNull(toSave.getItems());
    assertEquals(2, toSave.getItems().size());

    OrderItem s1 = toSave.getItems().get(0);
    OrderItem s2 = toSave.getItems().get(1);

    assertEquals("SKU1", s1.getSku());
    assertEquals("Apple", s1.getProductName());
    assertEquals(2, s1.getQuantity());
    assertEquals(new BigDecimal("10.00"), s1.getUnitPrice());
    assertEquals(toSave, s1.getOrder());

    assertEquals("SKU2", s2.getSku());
    assertEquals("Banana", s2.getProductName());
    assertEquals(3, s2.getQuantity());
    assertEquals(new BigDecimal("5.00"), s2.getUnitPrice());
    assertEquals(toSave, s2.getOrder());

    verify(orderRepository, times(1)).findWithItemsById(savedId);
    verify(orderMapper, times(1)).toDetails(savedWithItems);
  }

  @Test
  @DisplayName("после save не нашли заказ при reload -> NotFoundException")
  void create_WhenReloadNotFound_ShouldThrow() {
    UUID uid = userId();
    User u = user(uid);

    CreateOrderRequest dto = new CreateOrderRequest(uid, List.of(
        new CreateOrderItemRequest("SKU1", "Apple", 1, new BigDecimal("10.00"))
    ));

    when(userRepository.findById(uid)).thenReturn(Optional.of(u));

    UUID savedId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    when(orderRepository.save(any(Order.class))).thenReturn(Order.builder().id(savedId).build());
    when(orderRepository.findWithItemsById(savedId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> orderService.create(dto));

    verify(orderRepository).save(any(Order.class));
    verify(orderRepository).findWithItemsById(savedId);
    verifyNoInteractions(orderMapper);
  }

  @Test
  @DisplayName("заказ не найден -> NotFoundException")
  void update_WhenOrderNotFound_ShouldThrow() {
    UUID oid = orderId();
    when(orderRepository.findById(oid)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> orderService.update(oid, new UpdateOrderStatusRequest(OrderStatus.PAID)));

    verify(orderRepository, times(1)).findById(oid);
    verify(orderRepository, never()).save(any());
    verifyNoInteractions(orderMapper);
  }

  @Test
  @DisplayName("OK -> статус изменён, save, details")
  void update_WhenOrderExists_ShouldUpdateStatusAndReturnDetails() {
    UUID oid = orderId();
    UUID uid = userId();
    User u = user(uid);

    Order existing = order(oid, u, OrderStatus.NEW, new BigDecimal("35.00"), List.of());
    Order saved = order(oid, u, OrderStatus.PAID, new BigDecimal("35.00"), List.of());

    OrderDetailsResponse expected = mock(OrderDetailsResponse.class);

    when(orderRepository.findById(oid)).thenReturn(Optional.of(existing));
    when(orderRepository.save(existing)).thenReturn(saved);
    when(orderMapper.toDetails(saved)).thenReturn(expected);

    OrderDetailsResponse result = orderService.update(oid, new UpdateOrderStatusRequest(OrderStatus.PAID));

    assertNotNull(result);
    assertEquals(expected, result);
    assertEquals(OrderStatus.PAID, existing.getStatus());

    verify(orderRepository, times(1)).save(existing);
    verify(orderMapper, times(1)).toDetails(saved);
  }

  @Test
  @DisplayName("заказа нет -> NotFoundException")
  void delete_WhenOrderNotFound_ShouldThrow() {
    UUID oid = orderId();
    when(orderRepository.existsById(oid)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> orderService.delete(oid));

    verify(orderRepository, times(1)).existsById(oid);
    verify(orderRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("заказ есть -> deleteById вызывается")
  void delete_WhenOrderExists_ShouldDelete() {
    UUID oid = orderId();
    when(orderRepository.existsById(oid)).thenReturn(true);

    orderService.delete(oid);

    verify(orderRepository, times(1)).existsById(oid);
    verify(orderRepository, times(1)).deleteById(oid);
  }
}