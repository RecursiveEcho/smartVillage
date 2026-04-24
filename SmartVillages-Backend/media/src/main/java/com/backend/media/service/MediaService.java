package com.backend.media.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import com.backend.media.vo.UploadVO;
import com.backend.media.entity.MediaEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author chenyang
 * &#064;date 2026/4/20
 * &#064;description 媒体资源服务
 */
public interface MediaService extends IService<MediaEntity> {
    /**
     * 上传媒体资源
     * @param file 媒体资源文件
     * @param fileType 文件类型
     * @param category 分类
     * @param request 请求
     * @return 上传结果
     */
    UploadVO upload(MultipartFile file, String fileType, String category, HttpServletRequest request);
}