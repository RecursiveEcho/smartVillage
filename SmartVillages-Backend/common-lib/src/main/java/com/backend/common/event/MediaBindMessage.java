package com.backend.common.event;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 媒体绑定消息，用于 RabbitMQ 异步通知业务模块完成媒体绑定。 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaBindMessage implements Serializable {

  private static final long serialVersionUID = 1L;

  /** 上传者 ID（用于实体归属校验）。 */
  private Integer uploadUserId;

  /** OSS 文件 URL。 */
  private String fileUrl;

  /** 文件类型：image / video。 */
  private String fileType;

  /** 绑定目标模块：FEATURE / ANNOUNCEMENT / AUTH。 */
  private String bindTarget;

  /** 绑定到哪个实体。 */
  private Long bindEntityId;

  /** 绑定位置：COVER / IMAGES_APPEND / AVATAR。 */
  private String bindSlot;
}
