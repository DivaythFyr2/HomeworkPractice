package com.afavlad.homeworkpractice.service;

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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final OrderMapper orderMapper;

  public List<OrderSummaryResponse> getAllByUserId(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new NotFoundException("User not found: " + userId);
    }
    return orderRepository.findAllByUserId(userId)
        .stream()
        .map(orderMapper::toSummary)
        .toList();
  }

  public OrderDetailsResponse getById(UUID orderId) {
    Order order = orderRepository.findWithItemsById(orderId)
        .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
    return orderMapper.toDetails(order);
  }

  @Transactional
  public OrderDetailsResponse create(CreateOrderRequest dto) {
    User user = userRepository.findById(dto.userId())
        .orElseThrow(() -> new NotFoundException("User not found: " + dto.userId()));

    Order order = Order.builder()
        .user(user)
        .status(OrderStatus.NEW)
        .build();

    List<OrderItem> items = dto.items().stream()
        .map(i -> toItem(order, i))
        .toList();

    order.setItems(items);
    order.setTotalAmount(calcTotal(items));

    Order saved = orderRepository.save(order);
    Order savedWithItems = orderRepository.findWithItemsById(saved.getId())
        .orElseThrow(() -> new NotFoundException("Order not found: " + saved.getId()));

    return orderMapper.toDetails(savedWithItems);
  }

  @Transactional
  public OrderDetailsResponse update(UUID orderId, UpdateOrderStatusRequest dto) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

    order.setStatus(dto.status());
    Order savedOrder = orderRepository.save(order);
    return orderMapper.toDetails(savedOrder);
  }

  @Transactional
  public void delete(UUID orderId) {
    if (!orderRepository.existsById(orderId)) {
      throw new NotFoundException("Order not found: " + orderId);
    }
    orderRepository.deleteById(orderId);
  }

  private OrderItem toItem(Order order, CreateOrderItemRequest dto) {
    return OrderItem.builder()
        .order(order)
        .sku(dto.sku())
        .productName(dto.name())
        .quantity(dto.quantity())
        .unitPrice(dto.unitPrice())
        .build();
  }

  private BigDecimal calcTotal(List<OrderItem> items) {
    return items.stream()
        .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
