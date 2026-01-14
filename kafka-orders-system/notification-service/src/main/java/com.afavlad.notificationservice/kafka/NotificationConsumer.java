package com.afavlad.notificationservice.kafka;

import com.afavlad.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.afavlad.common.event.OrderEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

  private final NotificationService notificationService;

  @KafkaListener(
      topics = "sent_orders",
      concurrency = "3"
  )
  public void handleSentOrder(OrderEvent event) {
    log.info("Received sent order event: {}", event.orderId());
    notificationService.notifyUser(event);
  }
}
