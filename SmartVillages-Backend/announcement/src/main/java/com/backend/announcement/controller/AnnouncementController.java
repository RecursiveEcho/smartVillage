package com.backend.announcement.controller;

import com.backend.announcement.dto.AnnouncementCreateDTO;
import com.backend.announcement.dto.AnnouncementUpdateDTO;
import com.backend.announcement.service.AnnouncementService;
import com.backend.announcement.vo.AnnouncementVO;
import com.backend.common.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告 REST：前台读已发布列表/详情/热门；{@code /cadre/announcements} 下为村干部业务操作。
 * <p>
 * 热门接口使用静态子路径 {@code /announcements/hot}，避免与 {@code /announcements/{id}} 路由冲突。
 *
 * @author chenyang
 * &#064;date 2026/4/8
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "公告接口", description = "公告接口")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * @author chenyang
     * &#064;date 2026/4/8
     * &#064;description 村干部新增公告
     * @param dto 创建参数
     * @return 操作结果文案
     */
    @Operation(summary = "村干部新增公告")
    @PostMapping("/cadre/announcements")
    public Result<String> create(@Valid @RequestBody AnnouncementCreateDTO dto, HttpServletRequest request) {
        announcementService.createAnnouncement(dto, request);
        return Result.success("公告创建成功");
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/8
     * &#064;description 前台分页公告（仅已发布）
     * @param current 当前页
     * @param size    每页数量
     * @return 分页结果
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
     * &#064;date 2026/4/9
     * &#064;description 编辑公告基础信息
     * @param id  公告 ID
     * @param dto 更新参数
     * @return 操作结果文案
     */
    @Operation(summary = "编辑公告基础信息")
    @PutMapping("/cadre/announcements/{id}")
    public Result<String> update(@PathVariable Long id, @Valid @RequestBody AnnouncementUpdateDTO dto, HttpServletRequest request) {
        announcementService.updateAnnouncement(id, dto, request);
        return Result.success("公告编辑成功");
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/9
     * &#064;description 上架/下架公告
     * @param id     公告 ID
     * @param status 目标状态
     * @return 操作结果文案
     */
    @Operation(summary = "上架/下架公告")
    @PutMapping("/cadre/announcements/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id,
            @RequestParam Integer status, HttpServletRequest request) {
        announcementService.updateStatus(id, status, request);
        return Result.success("公告状态更新成功");
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/10
     * &#064;description 热门公告（静态路径，避免与 /{id} 冲突）
     * @param limit 条数上限
     * @return 热门列表
     */
    @Operation(summary = "热门公告")
    @GetMapping("/announcements/hot")
    public Result<List<AnnouncementVO>> getHotAnnouncements(
            @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(announcementService.listHot(limit));
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/9
     * &#064;description 公告详情
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
     * &#064;date 2026/4/15
     * &#064;description 村干部公告详情
     * @param id 公告 ID
     * @return 公告详情
     */
    @Operation(summary = "村干部公告详情")
    @GetMapping("/cadre/announcements/{id}")
    public Result<AnnouncementVO> getAdminAnnouncement(@PathVariable Long id) {
        return Result.success(announcementService.getAdminAnnouncement(id));
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/12
     * &#064;description 删除公告
     * @param id 公告 ID
     * @return 操作结果文案
     */
    @Operation(summary = "删除公告")
    @DeleteMapping("/cadre/announcements/{id}")
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success("公告删除成功");
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/14
     * &#064;description 村干部分页查询公告
     * @param current 当前页
     * @param size    每页数量
     * @param status  状态
     * @return 分页结果
     */
    @Operation(summary = "村干部分页查询公告")
    @GetMapping("/cadre/announcements")
    public Result<IPage<AnnouncementVO>> pageAdmin(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer isTop,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ) {
        return Result.success(
                announcementService.pageAdmin(current, size, status, title, type, isTop, startTime, endTime));
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/14
     * &#064;description 村干部待审核公告
     * @return 待审核公告列表
     */
    @Operation(summary = "村干部待审核公告")
    @GetMapping("/cadre/announcements/pending")
    public Result<IPage<AnnouncementVO>> pagePending(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer isTop,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ) {
        return Result.success(announcementService.pagePending(current, size, title, type, isTop, startTime, endTime));
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/14
     * &#064;description 村干部审核公告
     * @param id 公告 ID
     * @param status 状态
     * @return 操作结果文案
     */
    @Operation(summary = "村干部审核公告")
    @PutMapping("/cadre/announcements/{id}/audit")
    public Result<String> auditAnnouncement(
            @PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        announcementService.auditAnnouncement(id, status, request);
        return Result.success("公告审核成功");
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/15
     * &#064;description 审核历史列表
     * @param current 当前页
     * @param size    每页数量
     * @param title   标题
     * @param type    类型
     * @param isTop   是否置顶
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 审核历史列表
     */
    @Operation(summary = "审核历史列表")
    @GetMapping("/cadre/announcements/audited")
    public Result<IPage<AnnouncementVO>> pageAudited(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer isTop,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ) {
        return Result.success(announcementService.pageAudited(current, size, title, type, isTop, startTime, endTime));
    }
}
