package com.backend.media.service.impl;

import com.backend.common.context.LoginUserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.backend.media.service.MediaService;
import com.backend.media.vo.UploadVO;
import com.backend.media.tool.OssUploadTool;
import com.backend.common.exception.BusinessException;
import com.backend.common.enums.ErrorCode;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.backend.media.mapper.MediaMapper;
import com.backend.media.entity.MediaEntity;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
@Slf4j
/**
 * @author chenyang
 * &#064;date 2026/4/20
 * &#064;description 媒体资源服务实现
 */
@Service
@RequiredArgsConstructor
public class MediaServiceImpl extends ServiceImpl<MediaMapper, MediaEntity> implements MediaService {
    private final OssUploadTool ossUploadTool;

    @Override
    public UploadVO upload(MultipartFile file, String fileType, String category, HttpServletRequest request) {
        try {
            // 获取当前用户ID
            // 上传文件到OSS
            OssUploadTool.UploadResult uploadResult = ossUploadTool.uploadFile(
                    file.getInputStream(),
                    fileType,
                    file.getOriginalFilename(),
                    file.getContentType()
            );
            
            // 保存媒体资源到数据库
            MediaEntity mediaEntity = new MediaEntity();
            mediaEntity.setFileName(file.getOriginalFilename());
            mediaEntity.setFileUrl(uploadResult.url());
            mediaEntity.setFileType(fileType);
            mediaEntity.setFileSize(file.getSize());
            mediaEntity.setCategory(category);
            mediaEntity.setUploadUser(LoginUserContext.getAuthId(request));
            this.save(mediaEntity);
            
            return new UploadVO(
                file.getOriginalFilename(),
                file.getSize(),
                uploadResult.url(),
                uploadResult.objectKey());
        } catch (IOException e) {
            log.error("读取上传文件流失败，filename={}, fileType={}", file.getOriginalFilename(), fileType, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
}