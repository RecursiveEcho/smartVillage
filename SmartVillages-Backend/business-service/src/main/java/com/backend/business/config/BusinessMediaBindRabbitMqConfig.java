package com.backend.business.config;

import com.backend.common.mq.MediaBindMqNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BusinessMediaBindRabbitMqConfig {

  @Bean
  public DirectExchange mediaBindExchange() {
    return new DirectExchange(MediaBindMqNames.MEDIA_BIND_EXCHANGE);
  }

  @Bean
  public Queue mediaBindBusinessQueue() {
    return QueueBuilder.durable(MediaBindMqNames.MEDIA_BIND_BUSINESS_QUEUE)
        .deadLetterExchange(MediaBindMqNames.MEDIA_BIND_DLX)
        .deadLetterRoutingKey(MediaBindMqNames.MEDIA_BIND_DLQ_ROUTING_KEY)
        .build();
  }

  @Bean
public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
}

  @Bean
  public DirectExchange mediaBindDeadLetterExchange() {
    return new DirectExchange(MediaBindMqNames.MEDIA_BIND_DLX);
  }

    @Bean
  public Binding mediaBindDeadLetterBinding(
      Queue mediaBindDeadLetterQueue, DirectExchange mediaBindDeadLetterExchange) {
    return BindingBuilder.bind(mediaBindDeadLetterQueue)
        .to(mediaBindDeadLetterExchange)
        .with(MediaBindMqNames.MEDIA_BIND_DLQ_ROUTING_KEY);
  }

    @Bean
  public Queue mediaBindDeadLetterQueue() {
    return new Queue(MediaBindMqNames.MEDIA_BIND_DLQ);
  }

    @Bean
  public Binding mediaBindBusinessBinding(
      Queue mediaBindBusinessQueue, DirectExchange mediaBindExchange) {
    return BindingBuilder.bind(mediaBindBusinessQueue)
        .to(mediaBindExchange)
        .with(MediaBindMqNames.MEDIA_BIND_BUSINESS_ROUTING_KEY);
  }
}
