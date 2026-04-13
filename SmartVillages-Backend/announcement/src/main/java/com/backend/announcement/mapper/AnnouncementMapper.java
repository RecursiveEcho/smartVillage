package com.backend.announcement.mapper;

import com.backend.announcement.entity.AnnouncementEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * {@link AnnouncementEntity} 数据访问。
 *
 * @author chenyang
 * @date 2026/4/8
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<AnnouncementEntity> {
}
