package com.backend.authservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.backend.common.mq.MediaBindMqNames;

@Configuration
public class AuthMediaBindRabbitMqConfig {

  @Bean
  public DirectExchange mediaBindExchange(){
    return new DirectExchange(MediaBindMqNames.MEDIA_BIND_EXCHANGE);
  }

  @Bean
public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
}

 @Bean
  public Queue mediaBindAuthQueue() {
    return QueueBuilder.durable(MediaBindMqNames.MEDIA_BIND_AUTH_QUEUE)
        .deadLetterExchange(MediaBindMqNames.MEDIA_BIND_DLX)
        .deadLetterRoutingKey(MediaBindMqNames.MEDIA_BIND_DLQ_ROUTING_KEY)
        .build();
  }
  @Bean
  public DirectExchange mediaBindDeadLetterExchange() {
    return new DirectExchange(MediaBindMqNames.MEDIA_BIND_DLX);
  }

  @Bean
  public Queue mediaBindDeadLetterQueue() {
    return new Queue(MediaBindMqNames.MEDIA_BIND_DLQ);
  }

  @Bean
  public Binding mediaBindDeadLetterBinding(
      Queue mediaBindDeadLetterQueue, DirectExchange mediaBindDeadLetterExchange) {
    return BindingBuilder.bind(mediaBindDeadLetterQueue)
        .to(mediaBindDeadLetterExchange)
        .with(MediaBindMqNames.MEDIA_BIND_DLQ_ROUTING_KEY);
  }


  @Bean
  public Binding mediaBindAuthBinding(Queue mediaBindAuthQueue, DirectExchange mediaBindExchange) {
    return BindingBuilder.bind(mediaBindAuthQueue)
        .to(mediaBindExchange)
        .with(MediaBindMqNames.MEDIA_BIND_AUTH_ROUTING_KEY);
  }
}
