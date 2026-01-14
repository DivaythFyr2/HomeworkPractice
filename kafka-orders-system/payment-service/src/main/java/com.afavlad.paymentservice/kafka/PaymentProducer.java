package com.afavlad.paymentservice.kafka;

import com.afavlad.common.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProducer {

  private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

  public void sendPayedOrder(OrderEvent orderEvent) {
    kafkaTemplate.send("payed_orders", orderEvent.orderId().toString(), orderEvent);
    log.info("Payed order sent to Kafka: {}", orderEvent.orderId());
  }

}
