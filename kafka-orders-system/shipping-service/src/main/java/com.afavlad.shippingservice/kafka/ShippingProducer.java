package com.afavlad.shippingservice.kafka;

import com.afavlad.common.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShippingProducer {

  private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

  public void sendSentOrder(OrderEvent orderEvent) {
    kafkaTemplate.send("sent_orders", orderEvent.orderId().toString(), orderEvent);
    log.info("Sent order event published: {}", orderEvent.orderId());
  }
}
