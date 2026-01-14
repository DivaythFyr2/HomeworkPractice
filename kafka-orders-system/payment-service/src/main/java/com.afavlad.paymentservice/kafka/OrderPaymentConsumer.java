package com.afavlad.paymentservice.kafka;

import com.afavlad.common.event.OrderEvent;
import com.afavlad.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentConsumer {

  private final PaymentService paymentService;

  @KafkaListener(
      topics = "new_orders",
      concurrency = "3"
  )
  public void handleNewOrder(OrderEvent orderEvent) {
    log.info("Received order event: {}", orderEvent.orderId());
    paymentService.processPayment(orderEvent);
  }

}
