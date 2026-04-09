package com.backend.announcement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.backend.announcement.vo.AnnouncementVO;
import com.backend.announcement.dto.AnnouncementCreateDTO;
import com.backend.announcement.dto.AnnouncementUpdateDTO;
import com.backend.announcement.entity.AnnouncementEntity;
import com.baomidou.mybatisplus.extension.service.IService;
/**
 * @author chenyang
 * {@code @date} 2026/4/8
 * {@code @description} 公告服务接口
 */
public interface AnnouncementService extends IService<AnnouncementEntity> {
    IPage<AnnouncementVO> pagePublished(Long current, Long size);
    void create(AnnouncementCreateDTO dto);
    void updateAnnouncement(Long id, AnnouncementUpdateDTO dto);
    void updateStatus(Long id, Integer status);
    AnnouncementVO getAnnouncement(Long id);
}
