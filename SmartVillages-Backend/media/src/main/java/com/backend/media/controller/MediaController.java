package com.backend.media.controller;

import com.backend.common.result.Result;
import com.backend.media.service.MediaService;
import com.backend.media.vo.UploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author chenyang
 * @date 2026/4/20
 * @description 媒体资源控制器
 */
@Slf4j
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Tag(name = "媒体资源", description = "媒体资源接口")
public class MediaController {


    private final MediaService mediaService;

    /**
     * 上传图片
     * @param file 图片文件
     * @param fileType 文件类型
     * @return 上传结果
     */
    @Operation(summary = "上传图片")
    @PostMapping(value = "/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UploadVO> upload(
        @RequestParam("file") @NotNull MultipartFile file,
        @RequestParam("fileType") @NotBlank String fileType,
        @RequestParam("category") @NotBlank String category,
        HttpServletRequest request
    ) {
        UploadVO uploadVO = mediaService.upload(file, fileType,category, request);
        return Result.success(uploadVO);
    }

}