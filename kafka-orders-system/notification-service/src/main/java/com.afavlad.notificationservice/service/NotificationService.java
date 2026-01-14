package com.afavlad.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.afavlad.common.event.OrderEvent;

@Slf4j
@Service
public class NotificationService {

  public void notifyUser(OrderEvent orderEvent) {
    log.info(
        "User {} notified about delivery of order {}",
        orderEvent.userId(),
        orderEvent.orderId()
    );
  }

}
