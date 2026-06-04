package com.backend.mediaservice.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.backend.common.mq.MediaBindMqNames;


@Configuration
public class MediaBindRabbitMqConfig  {

  @Bean
  public DirectExchange mediaBindExchange() {
    return new DirectExchange(MediaBindMqNames.MEDIA_BIND_EXCHANGE);
  }

  @Bean
  public MessageConverter messageConverter(){
  return new Jackson2JsonMessageConverter();
  }
}
