package com.afavlad.orderservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  @Bean
  public NewTopic newOrdersTopic() {
    return TopicBuilder.name("new_orders")
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  public NewTopic payedOrdersTopic() {
    return TopicBuilder.name("payed_orders")
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  public NewTopic sentOrdersTopic() {
    return TopicBuilder.name("sent_orders")
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  public NewTopic newOrdersDltTopic() {
    return TopicBuilder.name("new_orders.DLT")
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  public NewTopic payedOrdersDltTopic() {
    return TopicBuilder.name("payed_orders.DLT")
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  public NewTopic sentOrdersDltTopic() {
    return TopicBuilder.name("sent_orders.DLT")
        .partitions(3)
        .replicas(1)
        .build();
  }
}
