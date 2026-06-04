package com.backend.business.mq;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.backend.common.binder.MediaBinder;
import com.backend.common.event.MediaBindMessage;
import com.backend.common.exception.BusinessException;
import com.backend.common.mq.MediaBindMqNames;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BusinessMediaBindMqConsumer {

  private final Map<String, MediaBinder> binderMap;

  public BusinessMediaBindMqConsumer(List<MediaBinder> binders) {
    this.binderMap =
        binders.stream()
            .collect(Collectors.toMap(MediaBinder::getSupportedTarget, Function.identity()));
  }

  @RabbitListener(queues = MediaBindMqNames.MEDIA_BIND_BUSINESS_QUEUE)
  public void onMessage(MediaBindMessage message) {
    String target = message.getBindTarget().trim().toUpperCase();
    MediaBinder binder = binderMap.get(target);
    if (binder == null) {
      throw new AmqpRejectAndDontRequeueException("business-service 不支持的 bindTarget: " + target);
    }

    try {
      binder.bindMedia(message);
      log.info(
          "business media bind consumed, target={}, entityId={}, slot={}",
          target,
          message.getBindEntityId(),
          message.getBindSlot());
    } catch (BusinessException e) {
      log.error(
          "business media bind failed, target={}, entityId={}, slot={}, fileUrl={}",
          target,
          message.getBindEntityId(),
          message.getBindSlot(),
          message.getFileUrl(),
          e);
          throw new AmqpRejectAndDontRequeueException("业务绑定失败，消息进入死信队列", e);
    }catch(Exception e){
      log.error(
        "business media bind system-failed, target={}, entityId={}, slot={}, fileUrl={}",
        target,
        message.getBindEntityId(),
        message.getBindSlot(),
        message.getFileUrl(),
        e);
        throw e;
    }
  }
}
