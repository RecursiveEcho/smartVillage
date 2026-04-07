package com.backend.admin.mapper;

import com.backend.admin.entity.AdminEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
/**
 * @author chenyang
 * @date 2026/4/7
 * @description 管理员数据访问接口
 */
/** {@link AdminEntity} 数据访问。 */
@Mapper
public interface AdminMapper extends BaseMapper<AdminEntity> {
}
