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
/**
 * @author chenyang
 * @date 2026/4/8
 * @description 
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name="公告接口",description="公告接口")
public class AnnouncementController {
    private final AnnouncementService announcementService;
    @Operation(summary = "管理员新增公告")
    @PostMapping("/admin/announcements")
    public Result<String> create(@RequestBody AnnouncementCreateDTO dto) {
        announcementService.create(dto);
        return Result.success("公告创建成功");
    }
    @Operation(summary = "前台分页公告（仅已发布）")
    @GetMapping("/announcements")
    public Result<IPage<AnnouncementVO>> pagePublished(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        return Result.success(announcementService.pagePublished(current, size));
    }
}