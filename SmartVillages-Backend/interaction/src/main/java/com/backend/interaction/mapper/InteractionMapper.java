package com.backend.interaction.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.backend.interaction.entity.InteractionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author chenyang
 * &#064;date 2026/4/15  
 * &#064;description 村民留言Mapper
 */
@Mapper
public interface InteractionMapper extends BaseMapper<InteractionEntity> {}