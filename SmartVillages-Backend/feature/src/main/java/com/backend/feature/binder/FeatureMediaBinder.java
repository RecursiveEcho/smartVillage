package com.backend.feature.binder;

import org.springframework.stereotype.Component;

import com.backend.common.binder.MediaBinder;
import com.backend.common.event.MediaBindAfterUploadEvent;
import com.backend.feature.service.FeatureService;

import lombok.RequiredArgsConstructor;

/**
 * Feature 模块的媒体绑定器，负责将上传的媒体绑定到 Feature 实体。
 */
@Component
@RequiredArgsConstructor
public class FeatureMediaBinder implements MediaBinder {

  private final FeatureService featureService;

    @Override
    public String getSupportedTarget(){
      return "FEATURE";
    }

    @Override
    public void bindMedia(MediaBindAfterUploadEvent event){
      featureService.bindUploadedMedia(
        event.getBindEntityId(),
        event.getBindSlot(),
        event.getFileUrl(),
        event.getFileType(),
        event.getRequest()
      );
    }


}
