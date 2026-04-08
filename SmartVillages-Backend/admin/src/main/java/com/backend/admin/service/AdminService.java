package com.backend.admin.service;

import com.backend.admin.vo.AdminVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.admin.entity.AdminEntity;

/**
 * @author chenyang
 * @date 2026/4/7
 * @description 管理员服务接口
 */
public interface AdminService extends IService<AdminEntity> {
    Page<AdminVO> pageUsers(String username, String role, Integer status, Long current, Long size);
    void updateUserStatus(Integer id, Integer status);
}