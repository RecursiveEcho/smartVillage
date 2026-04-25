package com.backend.media.controller;

import com.backend.common.result.Result;
import com.backend.media.service.MediaService;
import com.backend.media.vo.PageVO;
import com.backend.media.vo.UploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * @author chenyang
 * &#064;date 2026/4/20
 * &#064;description 媒体资源控制器
 */
@Slf4j
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Tag(name = "媒体资源", description = "媒体资源接口")
public class MediaController {


    private final MediaService mediaService;

    /**
     * 上传文件（兼容旧接口）
     * @param file 文件
     * @param fileType 文件类型
     * @return 上传结果
     */
    @Operation(summary = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UploadVO> upload(
        @RequestParam("file") MultipartFile file,
      @RequestParam("fileType") String fileType,
        @RequestParam("category") String category,  
        HttpServletRequest request
    ) {
        UploadVO uploadVO = mediaService.upload(file, fileType,category, request);
        return Result.success(uploadVO);
    }

    /**
     * 分页查询媒体资源
     * @param current 当前页
     * @param size 每页条数
     * @param fileType 文件类型
     * @param category 分类
     * @param status 状态
     * @return 分页查询结果
     */
    @Operation(summary = "分页查询媒体资源")
    @GetMapping("/cadre/media")
    public Result<IPage<PageVO>> page(
        @RequestParam(defaultValue = "1") Long current,
        @RequestParam(defaultValue = "10") Long size,
        @RequestParam(required = false) String fileType,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Integer status
    ) {
        return Result.success(mediaService.page(current, size, fileType, category, status));
    }
}