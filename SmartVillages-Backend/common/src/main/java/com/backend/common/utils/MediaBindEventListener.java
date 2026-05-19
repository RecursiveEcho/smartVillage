package com.backend.common.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.backend.common.binder.MediaBinder;
import com.backend.common.enums.ErrorCode;
import com.backend.common.event.MediaBindAfterUploadEvent;
import com.backend.common.exception.BusinessException;
/**
 * 媒体绑定事件监听器，负责监听 MediaBindAfterUploadEvent 事件，并委托对应的 MediaBinder 处理媒体绑定逻辑。
 */
@Component
public class MediaBindEventListener {

    private final Map<String, MediaBinder> binderMap;  // {"FEATURE"→FeatureBinder, "ANNOUNCEMENT"→AnnouncementBinder, ...}

    // 关键：Spring 会把所有实现了 MediaBinder 的 Bean 注入进来
    public MediaBindEventListener(List<MediaBinder> binders) {
        this.binderMap = binders.stream()
                .collect(Collectors.toMap(
                        MediaBinder::getSupportedTarget,  // key = "FEATURE" / "ANNOUNCEMENT" / "AUTH"
                        Function.identity()               // value = Binder 实例本身
                ));
    }

    @EventListener  // ← Spring 自动监听所有 ApplicationEvent
    public void onMediaUploaded(MediaBindAfterUploadEvent event) {
        String target = event.getBindTarget().trim().toUpperCase();  // "FEATURE"
        MediaBinder binder = binderMap.get(target);                  // 从 Map 找到对应 Binder
        if (binder == null) throw new BusinessException(ErrorCode.PARAM_INVALID, "不支持的 bindTarget: " + target);        // 不支持的目标，报错
        binder.bindMedia(event);                                     // 委托 Binder 处理
    }
}
