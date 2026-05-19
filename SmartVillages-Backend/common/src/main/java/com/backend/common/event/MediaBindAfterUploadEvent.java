package com.backend.common.event;

import org.springframework.context.ApplicationEvent;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
/**
 * 媒体绑定事件，在文件上传成功后触发，用于将上传的媒体绑定到业务实体。
 */
@Getter
public class MediaBindAfterUploadEvent extends ApplicationEvent {

    private final HttpServletRequest request;  // 当前请求（拿登录用户）
    private final String fileUrl;              // OSS 文件的 URL
    private final String fileType;             // 文件类型：image / video
    private final String bindTarget;           // 收件模块："FEATURE"/"ANNOUNCEMENT"/"AUTH"
    private final Long bindEntityId;           // 绑定到哪个实体（ID）
    private final String bindSlot;             // 绑定位置："COVER"/"IMAGES_APPEND"/"AVATAR"



    public MediaBindAfterUploadEvent(
            Object source,
            HttpServletRequest request,
            String fileUrl,
            String fileType,
            String bindTarget,
            Long bindEntityId,
            String bindSlot) {
        super(source);
        this.request = request;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.bindTarget = bindTarget;
        this.bindEntityId = bindEntityId;
        this.bindSlot = bindSlot;
    }
}
