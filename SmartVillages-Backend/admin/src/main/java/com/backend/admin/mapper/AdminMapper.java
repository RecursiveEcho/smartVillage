package com.backend.admin.mapper;

import com.backend.admin.entity.AdminEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/** {@link AdminEntity} 数据访问。 */
@Mapper
public interface AdminMapper extends BaseMapper<AdminEntity> {
}
