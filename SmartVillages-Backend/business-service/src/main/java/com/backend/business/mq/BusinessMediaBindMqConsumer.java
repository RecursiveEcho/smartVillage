package com.backend.business.mq;

import com.backend.common.binder.MediaBinder;
import com.backend.common.config.RabbitMqConfig;
import com.backend.common.enums.ErrorCode;
import com.backend.common.event.MediaBindMessage;
import com.backend.common.exception.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BusinessMediaBindMqConsumer {

  private final Map<String, MediaBinder> binderMap;

  public BusinessMediaBindMqConsumer(List<MediaBinder> binders) {
    this.binderMap =
        binders.stream()
            .collect(Collectors.toMap(MediaBinder::getSupportedTarget, Function.identity()));
  }

  @RabbitListener(queues = RabbitMqConfig.MEDIA_BIND_BUSINESS_QUEUE)
  public void onMessage(MediaBindMessage message) {
    String target = message.getBindTarget().trim().toUpperCase();
    MediaBinder binder = binderMap.get(target);
    if (binder == null) {
      throw new BusinessException(ErrorCode.PARAM_INVALID, "business-service 不支持的 bindTarget: " + target);
    }

    try {
      binder.bindMedia(message);
      log.info(
          "business media bind consumed, target={}, entityId={}, slot={}",
          target,
          message.getBindEntityId(),
          message.getBindSlot());
    } catch (Exception e) {
      log.error(
          "business media bind failed, target={}, entityId={}, slot={}, fileUrl={}",
          target,
          message.getBindEntityId(),
          message.getBindSlot(),
          message.getFileUrl(),
          e);
      throw e;
    }
  }
}
