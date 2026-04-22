package com.backend.admin.mapper;

import com.backend.admin.entity.AdminEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * {@link AdminEntity} 数据访问。
 *
 * @author chenyang
 * &#064;date 2026/4/7
 */
@Mapper
public interface AdminMapper extends BaseMapper<AdminEntity> {
}
