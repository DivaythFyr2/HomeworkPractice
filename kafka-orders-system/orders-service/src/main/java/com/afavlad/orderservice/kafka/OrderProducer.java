package com.afavlad.orderservice.kafka;

import com.afavlad.common.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

  private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

  public void sendNewOrder(OrderEvent orderEvent) {
    kafkaTemplate.send("new_orders", orderEvent.orderId().toString(), orderEvent);
    log.info("New order has been sent to topic: {}", orderEvent.orderId());
  }

}
