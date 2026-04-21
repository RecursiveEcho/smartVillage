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
     * 上传图片
     * @param inputStream 输入流
     * @param fileType 文件类型
     * @param originalFileName 原始文件名
     * @param contentType 内容类型
     * @return 上传结果
     */
    public UploadResult uploadImage(InputStream inputStream, String bizType, String originalFileName, String contentType) {
        if (!isAllowedType(bizType, contentType)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "文件类型与bizType不匹配，仅支持image/video/document");
        }
        OSS ossClient = null;
        try {
            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            // 生成对象键（同名文件自动追加(1)、(2)）
            String objectKey = buildObjectKey(ossClient, bizType, originalFileName);
            // 创建对象元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            // 上传文件到OSS
            ossClient.putObject(bucketName, objectKey, inputStream, metadata);
            // 生成文件URL
            String normalizedEndpoint = endpoint.replace("https://", "").replace("http://", "");
            String url = "https://" + bucketName + "." + normalizedEndpoint + "/" + objectKey;
            // 返回上传结果
            log.info("OSS上传成功，bizType={}, objectKey={}", bizType, objectKey);
            return new UploadResult(objectKey, url);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 判断文件类型是否允许
     * @param bizType 业务类型
     * @param contentType 内容类型
     * @return 是否允许
     */
    private boolean isAllowedType(String bizType, String contentType) {
        // 判断文件类型是否允许
        if (bizType.equals("image")) {
            return contentType.startsWith("image/");
        } else if ("video".equals(bizType)) {
            return contentType.startsWith("video/");
        } else if ("document".equals(bizType)) {
            return DOCUMENT_CONTENT_TYPES.contains(contentType);
        }
        return false;
    }

    /**
     * 按原始文件名生成对象键，若已存在则追加(1)、(2)...
     */
    private String buildObjectKey(OSS ossClient, String bizType, String originalFileName) {
        // 判断原始文件名是否为空
        String safeFileName = StringUtils.hasText(originalFileName) ? originalFileName.trim() : "file.bin";
        // 获取文件名中的点
        int dotIndex = safeFileName.lastIndexOf('.');
        // 获取文件名中的扩展名
        String baseName = dotIndex > 0 ? safeFileName.substring(0, dotIndex) : safeFileName;
        // 获取文件名中的扩展名
        String suffix = dotIndex > 0 ? safeFileName.substring(dotIndex) : ".bin";
        // 如果文件名中没有扩展名，则设置为file.bin
        if (!StringUtils.hasText(baseName)) {
            baseName = "file";
        }
        // 生成对象键
        String objectKey = bizType + "/" + baseName + suffix;
        int copyIndex = 1;
        // 如果对象键已存在，则追加(1)、(2)...
        while (ossClient.doesObjectExist(bucketName, objectKey)) {
            // 生成对象键
            objectKey = bizType + "/" + baseName + "(" + copyIndex + ")" + suffix;
            copyIndex++;
        }
        // 返回对象键
        return objectKey;
    }

    /**
     * 上传结果
     * @param objectKey 对象键
     * @param url 文件URL
     */
    public record UploadResult(String objectKey, String url) {}
}
