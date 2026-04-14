package com.backend.announcement.controller;

import com.backend.announcement.dto.AnnouncementCreateDTO;
import com.backend.announcement.dto.AnnouncementUpdateDTO;
import com.backend.announcement.service.AnnouncementService;
import com.backend.announcement.vo.AnnouncementVO;
import com.backend.common.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公告 REST：前台读已发布列表/详情/热门；{@code /admin/announcements} 下为后台增删改与上下架。
 * <p>
 * 热门接口使用静态子路径 {@code /announcements/hot}，避免与 {@code /announcements/{id}} 路由冲突。
 *
 * @author chenyang
 * @date 2026/4/8
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "公告接口", description = "公告接口")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * @author chenyang
     * @date 2026/4/8
     * @description 管理员新增公告
     * @param dto 创建参数
     * @return 操作结果文案
     */
    @Operation(summary = "管理员新增公告")
    @PostMapping("/admin/announcements")
    public Result<String> create(@Valid @RequestBody AnnouncementCreateDTO dto) {
        announcementService.create(dto);
        return Result.success("公告创建成功");
    }

    /**
     * @author chenyang
     * @date 2026/4/8
     * @description 前台分页公告（仅已发布）
     * @param current 当前页
     * @param size    每页数量
     * @return 分页结果
     */
    @Operation(summary = "前台分页公告（仅已发布）")
    @GetMapping("/announcements")
    public Result<IPage<AnnouncementVO>> pagePublished(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "当前页必须大于等于 1") Long current,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于等于 1")
            @Max(value = 100, message = "每页数量不能超过 100") Long size) {
        return Result.success(announcementService.pagePublished(current, size));
    }

    /**
     * @author chenyang
     * @date 2026/4/9
     * @description 编辑公告基础信息
     * @param id  公告 ID
     * @param dto 更新参数
     * @return 操作结果文案
     */
    @Operation(summary = "编辑公告基础信息")
    @PutMapping("/admin/announcements/{id}")
    public Result<String> update(@PathVariable Long id, @Valid @RequestBody AnnouncementUpdateDTO dto) {
        announcementService.updateAnnouncement(id, dto);
        return Result.success("公告编辑成功");
    }

    /**
     * @author chenyang
     * @date 2026/4/9
     * @description 上架/下架公告
     * @param id     公告 ID
     * @param status 目标状态
     * @return 操作结果文案
     */
    @Operation(summary = "上架/下架公告")
    @PutMapping("/admin/announcements/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id,
            @RequestParam @Min(value = 0, message = "状态仅支持 0 或 1")
            @Max(value = 1, message = "状态仅支持 0 或 1") Integer status) {
        announcementService.updateStatus(id, status);
        return Result.success("公告状态更新成功");
    }

    /**
     * @author chenyang
     * @date 2026/4/10
     * @description 热门公告（静态路径，避免与 /{id} 冲突）
     * @param limit 条数上限
     * @return 热门列表
     */
    @Operation(summary = "热门公告")
    @GetMapping("/announcements/hot")
    public Result<List<AnnouncementVO>> getHotAnnouncements(
            @RequestParam(defaultValue = "5")
            @Min(value = 1, message = "热门数量必须大于等于 1")
            @Max(value = 20, message = "热门数量不能超过 20") Integer limit) {
        return Result.success(announcementService.listHot(limit));
    }

    /**
     * @author chenyang
     * @date 2026/4/9
     * @description 公告详情
     * @param id 公告 ID
     * @return 公告详情
     */
    @Operation(summary = "公告详细")
    @GetMapping("/announcements/{id}")
    public Result<AnnouncementVO> getAnnouncement(@PathVariable Long id) {
        return Result.success(announcementService.getAnnouncement(id));
    }

    /**
     * @author chenyang
     * @date 2026/4/12
     * @description 删除公告
     * @param id 公告 ID
     * @return 操作结果文案
     */
    @Operation(summary = "删除公告")
    @DeleteMapping("/admin/announcements/{id}")
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success("公告删除成功");
    }

    /**
     * @author chenyang
     * @date 2026/4/14
     * @description 管理员分页查询公告
     * @param current 当前页
     * @param size    每页数量
     * @param status  状态
     * @return 分页结果
     */
    @Operation(summary = "管理员分页查询公告")
    @GetMapping("/admin/announcements")
    public Result<IPage<AnnouncementVO>> pageAdmin(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        return Result.success(announcementService.pageAdmin(current, size, status));
    }
}
