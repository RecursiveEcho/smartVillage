package com.backend.feature.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.backend.feature.entity.FeatureEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author chenyang
 * &#064;date 2026/4/23
 * &#064;description 乡村风采Mapper
 */
@Mapper
public interface FeatureMapper extends BaseMapper<FeatureEntity> {

}