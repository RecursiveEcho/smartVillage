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
import com.backend.common.exception.BusinessException;
import com.backend.announcement.dto.AnnouncementUpdateDTO;
import com.backend.common.enums.ErrorCode;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author chenyang
 * {@code @date} 2026/4/8
 * {@code @description} 公告服务实现类
 * {@code @author} chenyang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, AnnouncementEntity> implements AnnouncementService {
    private final AnnouncementMapper announcementMapper;
    @Override
    public void create(AnnouncementCreateDTO dto){
        AnnouncementEntity entity=new AnnouncementEntity();
        if(!StringUtils.hasText(dto.getTitle())){
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        entity.setTitle(dto.getTitle());
        if(!StringUtils.hasText(dto.getContent())){
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        entity.setContent(dto.getContent());
        entity.setStatus(1);
        if(dto.getType()==null||dto.getType()!=1&&dto.getType()!=2&&dto.getType()!=3){
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        entity.setType(dto.getType());
        if(dto.getIsTop()==null||dto.getIsTop()!=0&&dto.getIsTop()!=1){
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        entity.setIsTop(dto.getIsTop());
        entity.setPublishTime(LocalDateTime.now());
        entity.setViewCount(0);
        entity.setDeleted(0);
        this.save(entity);
    }

    @Override
    public IPage<AnnouncementVO> pagePublished(Long current,Long size){
        LambdaQueryWrapper<AnnouncementEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(AnnouncementEntity::getStatus,1)
               .eq(AnnouncementEntity::getDeleted,0)
               .orderByDesc(AnnouncementEntity::getIsTop)
               .orderByDesc(AnnouncementEntity::getPublishTime);
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
        vo.setAuditTime(entity.getAuditTime());
        vo.setAuditUser(entity.getAuditUser());
        vo.setViewCount(entity.getViewCount());
        vo.setCreateUser(entity.getCreateUser());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        vo.setDeleted(entity.getDeleted());
        return vo;
    }

    @Override
    public void updateAnnouncement(Long id, AnnouncementUpdateDTO dto){
        if(id==null){
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        AnnouncementEntity entity=this.getById(id);
        if(entity==null||Integer.valueOf(1).equals(entity.getDeleted()))
            {throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);}
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setType(dto.getType());
        entity.setIsTop(dto.getIsTop());
        if(!StringUtils.hasText(dto.getTitle())||!StringUtils.hasText(dto.getContent())
            ||dto.getType()==null||dto.getType()!=1&&dto.getType()!=2&&dto.getType()!=3){
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        this.updateById(entity);   
    }

    @Override
    public void updateStatus(Long id, Integer status){
        if(id==null){
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        AnnouncementEntity entity=this.getById(id);
        if(entity==null||Integer.valueOf(1).equals(entity.getDeleted())){throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);}
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ErrorCode.STATUS_INVALID);
        }
        entity.setStatus(status);
        if (Integer.valueOf(1).equals(status)) {
            if (entity.getPublishTime() == null) {
                entity.setPublishTime(LocalDateTime.now());
            }
            entity.setAuditTime(LocalDateTime.now());
        }
        this.updateById(entity);
    }

    @Override
    public AnnouncementVO getAnnouncement(Long id){
        if(id==null){
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        AnnouncementEntity entity=this.getById(id);
        if(entity==null||Integer.valueOf(1).equals(entity.getDeleted())){
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        Integer viewCount=entity.getViewCount()==null?0:entity.getViewCount();
        entity.setViewCount(viewCount+1);
        this.updateById(entity);
        return toVO(entity);
    }

    @Override
    public List<AnnouncementVO> listHot(Integer limit){
        if(limit==null||limit<=0){
            limit=5;
        }
        LambdaQueryWrapper<AnnouncementEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(AnnouncementEntity::getStatus,1)
               .eq(AnnouncementEntity::getDeleted,0)
               .orderByDesc(AnnouncementEntity::getViewCount)
               .orderByDesc(AnnouncementEntity::getCreateTime)
               .last("limit "+limit);
        List<AnnouncementEntity> list=announcementMapper.selectList(wrapper);
        return list.stream().map(this::toVO).toList();
    }
}