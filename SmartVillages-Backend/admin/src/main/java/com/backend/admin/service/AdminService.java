package com.backend.admin.service;

import com.backend.admin.entity.AdminEntity;
import com.backend.admin.vo.AdminVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AdminService extends IService<AdminEntity> {

    // 查询用户列表
    IPage<AdminVO> pageUsers(String username, String role, Integer status, Long current, Long size);

    // 更新用户状态
    void updateUserStatus(Integer id, Integer status);
}
