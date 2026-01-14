package com.afavlad.orderservice.service;

import com.afavlad.common.event.OrderEvent;
import com.afavlad.orderservice.kafka.OrderProducer;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderProducer orderProducer;

  public void createOrder(UUID userId) {
    OrderEvent orderEvent = OrderEvent.builder()
        .orderId(UUID.randomUUID())
        .userId(userId)
        .status("NEW")
        .createdAt(OffsetDateTime.now())
        .build();

    orderProducer.sendNewOrder(orderEvent);
    log.info("Order created: {}", orderEvent.orderId());
  }

}
