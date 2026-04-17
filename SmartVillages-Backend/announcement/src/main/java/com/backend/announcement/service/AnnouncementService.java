package com.backend.announcement.service;

import com.backend.announcement.dto.AnnouncementCreateDTO;
import com.backend.announcement.dto.AnnouncementUpdateDTO;
import com.backend.announcement.entity.AnnouncementEntity;
import com.backend.announcement.vo.AnnouncementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AnnouncementService extends IService<AnnouncementEntity> {

    // 管理员新增公告
    void createAnnouncement(AnnouncementCreateDTO dto, HttpServletRequest request);

    // 前台分页公告（仅已发布）
    IPage<AnnouncementVO> pagePublished(Long current, Long size);

    // 编辑公告基础信息
    void updateAnnouncement(Long id, AnnouncementUpdateDTO dto, HttpServletRequest request);

    // 上架/下架公告
    void updateStatus(Long id, Integer status, HttpServletRequest request);

    // 管理员审核公告
    void auditAnnouncement(Long id, Integer status, HttpServletRequest request);

    // 公告详情
    AnnouncementVO getAnnouncement(Long id);

    // 管理员公告详情（审核回显）
    AnnouncementVO getAdminAnnouncement(Long id);

    // 热门公告（静态路径，避免与 /{id} 冲突）
    List<AnnouncementVO> listHot(Integer limit);

    // 删除公告
    void deleteAnnouncement(Long id);

    // 管理员分页查询公告
    IPage<AnnouncementVO> pageAdmin(
            Long current,
            Long size,
            Integer status,
            String title,
            Integer type,
            Integer isTop,
            LocalDateTime startTime,
            LocalDateTime endTime);

    // 管理员待审核公告
    IPage<AnnouncementVO> pagePending(
            Long current,
            Long size,
            String title,
            Integer type,
            Integer isTop,
            LocalDateTime startTime,
            LocalDateTime endTime);

    // 管理员审核历史列表
    IPage<AnnouncementVO> pageAudited(
            Long current,
            Long size,
            String title,
            Integer type,
            Integer isTop,
            LocalDateTime startTime,
            LocalDateTime endTime);
}
