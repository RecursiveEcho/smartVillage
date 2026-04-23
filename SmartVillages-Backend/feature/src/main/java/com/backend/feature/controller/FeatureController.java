package com.backend.feature.controller;

import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.backend.feature.service.FeatureService;
import com.backend.common.result.Result;
import com.backend.feature.dto.HighlightCreateDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.backend.feature.vo.FeatureVO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
/**
 * @author chenyang
 * &#064;date 2026/4/23
 * &#064;description 乡村风采控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "乡村风采", description = "乡村风采接口")
public class FeatureController {

    private final FeatureService featureService; 

    /*  
    * 创建乡村风采
    * @author chenyang
     * &#064;date 2026/4/23
     * &#064;description 乡村风采创建
     * @param dto 乡村风采创建DTO
     * @return 乡村风采创建成功
     */
    @Operation(summary = "村干部创建乡村风采")
    @PostMapping("/cadre/features")
    public Result<String> createFeature(@Valid @RequestBody HighlightCreateDTO dto, HttpServletRequest request) {
        featureService.createFeature(dto, request);
        return Result.success("乡村风采创建成功");
    }

    /*  
    * 获取乡村风采列表(村民可见)
    * @author chenyang
    * &#064;date 2026/4/23
    * &#064;description 乡村风采列表
    * @param current 当前页
    * @param size 每页条数
    * @param type 类型
    * @param getSort 排序
    * @param getCreateTime 创建时间
    * @param start 开始时间
    * @param end 结束时间
    * @param request HTTP请求
    * @return 乡村风采列表
    */
    @Operation(summary = "获取乡村风采列表(村民可见)")
    @GetMapping("/features")
    public Result<IPage<FeatureVO>> getFeatureList(
        @RequestParam(defaultValue = "1") Long current,
        @RequestParam(defaultValue = "10") Long size,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) Integer getSort,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime getCreateTime,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
        HttpServletRequest request) {
        return Result.success(featureService.getFeatureList(current, size, type, getSort, getCreateTime, startTime, endTime, request));
    }

    /*  
    * 获取乡村风采详情
    * @author chenyang
    * &#064;date 2026/4/23
    * &#047;description 乡村风采详情
    * @param id 乡村风采ID
    * @return 乡村风采详情
    */
    @Operation(summary = "获取乡村风采详情")
    @GetMapping("/features/{id}")
    public Result<FeatureVO> getFeatureDetail(@PathVariable Long id) {
        return Result.success(featureService.getFeatureDetail(id));
    }   

    /*  
    * 上下架乡村风采
    * @author chenyang
    * &#064;date 2026/4/23
    * &#064;description 上下架乡村风采
    * @param id 乡村风采ID
    * @param status 状态
    * @param request HTTP请求
    * @return 乡村风采上下架成功
    */
    @Operation(summary = "上下架乡村风采")  
    @PutMapping("/cadre/features/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        featureService.updateStatus(id, status, request);
        return Result.success("乡村风采上下架成功");
    }

    /*  
    * 管理端获取乡村风采列表
    * @author chenyang
    * &#064;date 2026/4/23
    * &#064;description 管理端获取乡村风采列表
    * @param current 当前页
    * @param size 每页条数
    * @param title 标题
    * @param type 类型
    * @param getSort 排序
    * @param getCreateTime 创建时间
    * @param startTime 开始时间
    * @param endTime 结束时间
    * @param request HTTP请求
    * @return 乡村风采列表
    */
    @Operation(summary = "管理端获取乡村风采列表")
    @GetMapping("/cadre/features")
    public Result<IPage<FeatureVO>> getFeatureListByAdmin(
        @RequestParam(defaultValue = "1") Long current,
        @RequestParam(defaultValue = "10") Long size,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) Integer getSort,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime getCreateTime,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
        HttpServletRequest request) {
        return Result.success(featureService.getFeatureListByAdmin(current, size, status, title, type, getSort, getCreateTime, startTime, endTime, request));
    }

    @Operation(summary = "修改乡村风采")
    @PutMapping("/cadre/features/{id}")
    public Result<String> updateFeature(@PathVariable Long id, @Valid @RequestBody HighlightCreateDTO dto, HttpServletRequest request) {
        featureService.updateFeature(id, dto, request);
        return Result.success("乡村风采修改成功");
    }
}