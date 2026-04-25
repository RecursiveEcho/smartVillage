package com.backend.media.tool;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.io.InputStream;
import java.util.Set;

@Slf4j
@Component
public class OssUploadTool {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    // 文档类型
    private static final Set<String> DOCUMENT_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "text/plain"
    );

    /**
     * 上传文件
     * @param inputStream 输入流
     * @param fileType 文件类型
     * @param originalFileName 原始文件名
     * @param contentType 内容类型
     * @return 上传结果
     */
    public UploadResult uploadFile(InputStream inputStream, String fileType, String originalFileName, String contentType) {
        // 判断文件类型是否允许
        if (!isAllowedType(fileType, contentType)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "文件类型与fileType不匹配，仅支持image/video/document");
        }
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 生成对象键（同名文件自动追加(1)、(2)）
            String objectKey = buildObjectKey(ossClient, fileType, originalFileName);
            // 设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            // 上传文件到OSS
            ossClient.putObject(bucketName, objectKey, inputStream, metadata);
            String normalizedEndpoint = endpoint.replaceAll("^https?://", "");
            String url = "https://" + bucketName + "." + normalizedEndpoint + "/" + objectKey;
            // 返回上传结果
            log.info("OSS上传成功，fileType={}, objectKey={}", fileType, objectKey);
            return new UploadResult(objectKey, url);
        } catch (Exception e) {
            log.error("OSS上传失败，fileType={}, originalFileName={}", fileType, originalFileName, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 判断文件类型是否允许
     * @param fileType 文件类型
     * @param contentType 内容类型
     * @return 是否允许
     */
    private boolean isAllowedType(String fileType, String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return false;
        }
        // 判断文件类型是否允许
        if ("image".equals(fileType)) {
            return contentType.startsWith("image/");
        } else if ("video".equals(fileType)) {
            return contentType.startsWith("video/");
        } else if ("document".equals(fileType)) {
            return DOCUMENT_CONTENT_TYPES.contains(contentType);
        }
        return false;
    }

    /**
     * 按原始文件名生成对象键，若已存在则追加(1)、(2)...
     */
    private String buildObjectKey(OSS ossClient, String fileType, String originalFileName) {
        if (!StringUtils.hasText(originalFileName)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "文件名不能为空");
        }
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex < 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "文件名不合法");
        }
        String baseName = originalFileName.substring(0, dotIndex);
        // 文件名后缀
        String suffix = originalFileName.substring(dotIndex);

        String objectPrefix = fileType + "/";
        String objectKey = objectPrefix + baseName + suffix;
        int copyIndex = 1;

        // 判断对象是否存在，存在则追加(1)、(2)...
        while (ossClient.doesObjectExist(bucketName, objectKey)) {
            objectKey = objectPrefix + baseName + "(" + copyIndex + ")" + suffix;
            copyIndex++;
        }
        return objectKey;
    }

    /**
     * 上传结果
     * @param objectKey 对象键
     * @param url 文件URL
     */
    public record UploadResult(String objectKey, String url) {}
}
