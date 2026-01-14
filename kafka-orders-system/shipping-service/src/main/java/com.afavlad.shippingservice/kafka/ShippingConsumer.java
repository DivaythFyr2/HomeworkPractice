package com.afavlad.shippingservice.kafka;

import com.afavlad.common.event.OrderEvent;
import com.afavlad.shippingservice.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShippingConsumer {

  private final ShippingService shippingService;

  @KafkaListener(
      topics = "payed_orders",
      concurrency = "3"
  )
  public void handlePayedOrder(OrderEvent event) {
    log.info("Received payed order: {}", event.orderId());
    shippingService.shipOrder(event);
  }
}
