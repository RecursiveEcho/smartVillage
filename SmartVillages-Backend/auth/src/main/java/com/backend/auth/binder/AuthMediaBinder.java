package com.backend.auth.binder;

import com.backend.auth.service.AuthService;
import com.backend.common.binder.MediaBinder;
import com.backend.common.event.MediaBindAfterUploadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMediaBinder implements MediaBinder {

  private final AuthService authService;

  @Override
  public String getSupportedTarget() {
    return "AUTH";
  }

  @Override
  public void bindMedia(MediaBindAfterUploadEvent event) {
    authService.bindUploadedMedia(
        event.getBindEntityId(),
        event.getBindSlot(),
        event.getFileUrl(),
        event.getFileType(),
        event.getUploadUserId());
  }
}
