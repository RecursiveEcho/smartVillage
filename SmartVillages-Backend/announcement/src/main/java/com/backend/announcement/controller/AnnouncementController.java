package com.backend.announcement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.backend.announcement.service.AnnouncementService;
import com.backend.common.result.Result;
import org.springframework.web.bind.annotation.RequestParam;
import com.backend.announcement.vo.AnnouncementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.backend.announcement.dto.AnnouncementCreateDTO;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.backend.announcement.dto.AnnouncementUpdateDTO;
import java.util.List;
/**
 * @author chenyang
 * {@code @date} 2026/4/8
 * {@code @description} 公告控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name="公告接口",description="公告接口")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    /**
     * @author chenyang
     * @date 2026/4/8
     * @description 管理员新增公告
     * @param dto
     */
    @Operation(summary = "管理员新增公告")
    @PostMapping("/admin/announcements")
    public Result<String> create(@RequestBody AnnouncementCreateDTO dto) {
        announcementService.create(dto);
        return Result.success("公告创建成功");
    }

    /**
     * @author chenyang
     * @date 2026/4/8
     * @description 前台分页公告（仅已发布）
     * 前台分页公告（仅已发布）
     * @param current
     * @param size
     * @return
     */
    @Operation(summary = "前台分页公告（仅已发布）")
    @GetMapping("/announcements")
    public Result<IPage<AnnouncementVO>> pagePublished(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        return Result.success(announcementService.pagePublished(current, size));
    }


    /**
     * @author chenyang
     * @date 2026/4/9
     * @description 编辑公告基础信息
     * @param id
     * @param dto
     */
    @Operation(summary = "编辑公告基础信息")
    @PutMapping("/admin/announcements/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody AnnouncementUpdateDTO dto) {
        announcementService.updateAnnouncement(id, dto);  
        return Result.success("公告编辑成功");
    }

    @Operation(summary = "上架/下架公告")
    @PutMapping("/admin/announcements/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        announcementService.updateStatus(id, status);
        return Result.success("公告状态更新成功");
    }

    
    /**
     * @author chenyang
     * @date 2026/4/9
     * @description 公告详细
     * @param id 公告ID
     * @return 公告详细
     */
    @Operation(summary="公告详细")
    @GetMapping("/announcements/{id}")
    public Result<AnnouncementVO> getAnnouncement(@PathVariable Long id) {
        return Result.success(announcementService.getAnnouncement(id));
    }

    /**
     * @author chenyang
     * @date 2026/4/10
     * @description 热门公告
     * @return 热门公告
     */
    @Operation(summary="热门公告")
    @GetMapping("/announcements/hot")
    public Result<List<AnnouncementVO>> getHotAnnouncements(@RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(announcementService.listHot(limit));
    }
}