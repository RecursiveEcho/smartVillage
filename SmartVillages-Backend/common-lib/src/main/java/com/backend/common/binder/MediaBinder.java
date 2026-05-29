package com.backend.common.binder;

import com.backend.common.event.MediaBindMessage;

/** 媒体绑定器，用于将上传的媒体绑定到业务实体。 */
public interface MediaBinder {
  /**
   * 获取支持的媒体类型。
   *
   * @return 媒体类型
   */
  String getSupportedTarget();

  /** 绑定媒体。 */
  void bindMedia(MediaBindMessage message);
}
