package com.afavlad.paymentservice.service;

import com.afavlad.common.event.OrderEvent;
import com.afavlad.paymentservice.kafka.PaymentProducer;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentProducer paymentProducer;

  public void processPayment(OrderEvent orderEvent) {
    log.info("Processing payment for order {}", orderEvent.orderId());

    OrderEvent payedEvent = OrderEvent.builder()
        .orderId(orderEvent.orderId())
        .userId(orderEvent.userId())
        .status("PAYED")
        .createdAt(OffsetDateTime.now())
        .build();

    paymentProducer.sendPayedOrder(payedEvent);
    log.info("Payment completed for order {}", orderEvent.orderId());
  }

}
