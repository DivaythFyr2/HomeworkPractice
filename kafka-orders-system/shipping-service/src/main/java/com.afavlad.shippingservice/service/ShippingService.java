package com.afavlad.shippingservice.service;

import com.afavlad.common.event.OrderEvent;
import com.afavlad.shippingservice.kafka.ShippingProducer;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {

  private final ShippingProducer shippingProducer;

  public void shipOrder(OrderEvent orderEvent) {
    log.info("Order has been sent to topic: {}", orderEvent.orderId());

    OrderEvent sentEvent = OrderEvent.builder()
        .orderId(orderEvent.orderId())
        .userId(orderEvent.userId())
        .status("SENT")
        .createdAt(OffsetDateTime.now())
        .build();

    shippingProducer.sendSentOrder(sentEvent);
    log.info("Order shipped {}", orderEvent.orderId());
  }

}
