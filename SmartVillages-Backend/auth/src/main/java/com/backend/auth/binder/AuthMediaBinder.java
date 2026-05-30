package com.backend.auth.binder;

import org.springframework.stereotype.Component;

import com.backend.auth.service.AuthService;
import com.backend.common.binder.MediaBinder;
import com.backend.common.event.MediaBindMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthMediaBinder implements MediaBinder {

  private final AuthService authService;

  @Override
  public String getSupportedTarget() {
    return "AUTH";
  }

  @Override
  public void bindMedia(MediaBindMessage message) {
    authService.bindUploadedMedia(
        message.getBindEntityId(),
        message.getBindSlot(),
        message.getFileUrl(),
        message.getFileType(),
        message.getUploadUserId());
  }
}
