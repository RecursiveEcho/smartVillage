package com.backend.announcement.service.impl;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.backend.announcement.service.AnnouncementService;
import com.backend.announcement.mapper.AnnouncementMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backend.announcement.vo.AnnouncementVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.announcement.dto.AnnouncementCreateDTO;
import com.backend.announcement.entity.AnnouncementEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
/**
 * @author chenyang
 * @date 2026/4/8
 * @description 公告服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, AnnouncementEntity> implements AnnouncementService {
    private final AnnouncementMapper announcementMapper;
    @Override
    public void create(AnnouncementCreateDTO dto){
        AnnouncementEntity entity=new AnnouncementEntity();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setStatus(dto.getStatus());
        entity.setType(1);
        entity.setIsTop(0);
        entity.setViewCount(0);
        entity.setDeleted(0);
        this.save(entity);
    }
    @Override
    public IPage<AnnouncementVO> pagePublished(Long current,Long size){
        LambdaQueryWrapper<AnnouncementEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(AnnouncementEntity::getStatus,1)
               .orderByDesc(AnnouncementEntity::getCreateTime);
        Page<AnnouncementEntity> page=announcementMapper.selectPage(new Page<>(current,size), wrapper);
        return page.convert(this::toVO);
    }

    private AnnouncementVO toVO(AnnouncementEntity entity){
        AnnouncementVO vo=new AnnouncementVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setStatus(entity.getStatus());
        vo.setType(entity.getType());
        vo.setIsTop(entity.getIsTop());
        vo.setPublishTime(entity.getPublishTime());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}