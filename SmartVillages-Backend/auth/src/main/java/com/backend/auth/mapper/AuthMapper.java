package com.backend.auth.mapper;

import com.backend.auth.entity.AuthEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author chenyang
 * @date 2026/4/2
 * @description 权限 Mapper层
 */
@Mapper
public interface AuthMapper extends BaseMapper<AuthEntity> {
}
