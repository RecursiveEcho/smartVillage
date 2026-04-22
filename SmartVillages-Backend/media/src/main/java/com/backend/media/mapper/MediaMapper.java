package com.backend.media.mapper;

import com.backend.media.entity.MediaEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * {@link MediaEntity} 数据访问。
 *
 * @author chenyang
 * &#064;date 2026/4/20
 * &#064;description 媒体资源Mapper
 */
@Mapper
public interface MediaMapper extends BaseMapper<MediaEntity> {}