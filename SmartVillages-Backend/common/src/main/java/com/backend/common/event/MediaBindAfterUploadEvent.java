package com.backend.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/** 媒体绑定事件，在审核通过后触发，用于将上传的媒体绑定到业务实体。 */
@Getter
public class MediaBindAfterUploadEvent extends ApplicationEvent {

  /** 上传者 ID（用于实体归属校验）。 */
  private final Integer uploadUserId;

  /** OSS 文件 URL。 */
  private final String fileUrl;

  /** 文件类型：image / video。 */
  private final String fileType;

  /** 绑定目标模块：FEATURE / ANNOUNCEMENT / AUTH。 */
  private final String bindTarget;

  /** 绑定到哪个实体。 */
  private final Long bindEntityId;

  /** 绑定位置：COVER / IMAGES_APPEND / AVATAR。 */
  private final String bindSlot;

  public MediaBindAfterUploadEvent(
      Object source,
      Integer uploadUserId,
      String fileUrl,
      String fileType,
      String bindTarget,
      Long bindEntityId,
      String bindSlot) {
    super(source);
    this.uploadUserId = uploadUserId;
    this.fileUrl = fileUrl;
    this.fileType = fileType;
    this.bindTarget = bindTarget;
    this.bindEntityId = bindEntityId;
    this.bindSlot = bindSlot;
  }
}
