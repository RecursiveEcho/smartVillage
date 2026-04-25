package com.backend.media.controller;

import com.backend.common.result.Result;
import com.backend.media.service.MediaService;
import com.backend.media.vo.PageVO;
import com.backend.media.vo.DetailVO;
import com.backend.media.vo.UploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
/**
 * @author chenyang
 * &#064;date 2026/4/20
 * &#064;description 媒体资源控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "媒体资源", description = "媒体资源接口")
@RequestMapping("/media")
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
    @GetMapping("/page")
    public Result<IPage<PageVO>> page(
        @RequestParam(defaultValue = "1") Long current,
        @RequestParam(defaultValue = "10") Long size,
        @RequestParam(required = false) String fileType,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Integer status
    ) {
        return Result.success(mediaService.page(current, size, fileType, category, status));
    }

    /**
     * 删除媒体资源
     * @param id 媒体资源id
     * @return 删除结果
     */
    @Operation(summary = "删除媒体资源")
    @DeleteMapping("/cadre/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        mediaService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 获取媒体资源详情
     * @param id 媒体资源id
     * @return 媒体资源详情
     */
    @Operation(summary = "获取媒体资源详情")
    @GetMapping("/{id}")
    public Result<DetailVO> getDetail(@PathVariable Integer id) {
        return Result.success(mediaService.getDetail(id));
    }

    /**
     * 启用/禁用媒体资源
     * @param id 媒体资源id
     * @param status 状态 0-禁用 1-启用
     * @return 启用/禁用结果
     */
    @Operation(summary = "启用/禁用媒体资源")
    @PutMapping("/cadre/{id}/status")
    public Result<String> updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        mediaService.updateStatus(id, status);
        return Result.success("操作媒体资源状态成功");
    }
}