package com.backend.auth.mapper;

import com.backend.auth.entity.AuthEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * {@link AuthEntity} 数据访问。
 *
 * @author chenyang
 * &#064;date 2026/4/2
 */
@Mapper
public interface AuthMapper extends BaseMapper<AuthEntity> {
}
