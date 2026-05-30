package com.backend.announcement.binder;

import org.springframework.stereotype.Component;

import com.backend.announcement.service.AnnouncementService;
import com.backend.common.binder.MediaBinder;
import com.backend.common.event.MediaBindMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnnouncementMediaBinder implements MediaBinder {

  private final AnnouncementService announcementService;

  @Override
  public String getSupportedTarget() {
    return "ANNOUNCEMENT";
  }

  @Override
  public void bindMedia(MediaBindMessage message) {
    announcementService.bindUploadedMedia(
        message.getBindEntityId(),
        message.getFileUrl(),
        message.getFileType(),
        message.getBindSlot(),
        message.getUploadUserId());
  }
}
