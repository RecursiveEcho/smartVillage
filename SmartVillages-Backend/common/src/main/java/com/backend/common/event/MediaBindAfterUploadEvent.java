package com.backend.common.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * 媒体绑定事件，在审核通过后触发，用于将上传的媒体绑定到业务实体。
 */
@Getter
public class MediaBindAfterUploadEvent extends ApplicationEvent {

    private final Integer uploadUserId;         // 上传者 ID（用于实体归属校验）
    private final String fileUrl;               // OSS 文件的 URL
    private final String fileType;              // 文件类型：image / video
    private final String bindTarget;            // 收件模块："FEATURE"/"ANNOUNCEMENT"/"AUTH"
    private final Long bindEntityId;            // 绑定到哪个实体（ID）
    private final String bindSlot;              // 绑定位置："COVER"/"IMAGES_APPEND"/"AVATAR"

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
