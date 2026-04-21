package com.backend.media.mapper;

import com.backend.media.entity.MediaEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * {@link MediaEntity} 数据访问。
 *
 * @author chenyang
 * @date 2026/4/20
 * @description 媒体资源Mapper
 */
@Mapper
public interface MediaMapper extends BaseMapper<MediaEntity> {}