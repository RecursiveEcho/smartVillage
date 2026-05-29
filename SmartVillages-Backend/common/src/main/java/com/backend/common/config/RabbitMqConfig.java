package com.backend.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

  public static final String MEDIA_BIND_EXCHANGE = "media.bind.exchange";
  public static final String MEDIA_BIND_BUSINESS_QUEUE = "media.bind.business.queue";
  public static final String MEDIA_BIND_BUSINESS_ROUTING_KEY = "media.bind.business";
  public static final String MEDIA_BIND_AUTH_QUEUE = "media.bind.auth.queue";
  public static final String MEDIA_BIND_AUTH_ROUTING_KEY = "media.bind.auth";
  public static final String MEDIA_BIND_DLX = "media.bind.dlx";
  public static final String MEDIA_BIND_DLQ = "media.bind.dlq";
  public static final String MEDIA_BIND_DLQ_ROUTING_KEY = "media.bind.dlq";

  @Bean
  public DirectExchange mediaBindExchange() {
    return new DirectExchange(MEDIA_BIND_EXCHANGE);
  }

  @Bean
  public Queue mediaBindBusinessQueue() {
    return QueueBuilder.durable(MEDIA_BIND_BUSINESS_QUEUE)
        .deadLetterExchange(MEDIA_BIND_DLX)
        .deadLetterRoutingKey(MEDIA_BIND_DLQ_ROUTING_KEY)
        .build();
  }

  @Bean
  public Queue mediaBindAuthQueue() {
    return QueueBuilder.durable(MEDIA_BIND_AUTH_QUEUE)
        .deadLetterExchange(MEDIA_BIND_DLX)
        .deadLetterRoutingKey(MEDIA_BIND_DLQ_ROUTING_KEY)
        .build();
  }

  @Bean
  public DirectExchange mediaBindDeadLetterExchange() {
    return new DirectExchange(MEDIA_BIND_DLX);
  }

  @Bean
  public Queue mediaBindDeadLetterQueue() {
    return new Queue(MEDIA_BIND_DLQ);
  }

  @Bean
  public Binding mediaBindDeadLetterBinding(
      Queue mediaBindDeadLetterQueue, DirectExchange mediaBindDeadLetterExchange) {
    return BindingBuilder.bind(mediaBindDeadLetterQueue)
        .to(mediaBindDeadLetterExchange)
        .with(MEDIA_BIND_DLQ_ROUTING_KEY);
  }

  @Bean
  public Binding mediaBindBusinessBinding(
      Queue mediaBindBusinessQueue, DirectExchange mediaBindExchange) {
    return BindingBuilder.bind(mediaBindBusinessQueue)
        .to(mediaBindExchange)
        .with(MEDIA_BIND_BUSINESS_ROUTING_KEY);
  }

  @Bean
  public Binding mediaBindAuthBinding(Queue mediaBindAuthQueue, DirectExchange mediaBindExchange) {
    return BindingBuilder.bind(mediaBindAuthQueue)
        .to(mediaBindExchange)
        .with(MEDIA_BIND_AUTH_ROUTING_KEY);
  }
}
