package com.backend.announcement.binder;

import com.backend.announcement.service.AnnouncementService;
import com.backend.common.binder.MediaBinder;
import com.backend.common.event.MediaBindAfterUploadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnnouncementMediaBinder implements MediaBinder {

  private final AnnouncementService announcementService;

  @Override
  public String getSupportedTarget() {
    return "ANNOUNCEMENT";
  }

  @Override
  public void bindMedia(MediaBindAfterUploadEvent event) {
    announcementService.bindUploadedMedia(
        event.getBindEntityId(),
        event.getFileUrl(),
        event.getFileType(),
        event.getBindSlot(),
        event.getUploadUserId());
  }
}
