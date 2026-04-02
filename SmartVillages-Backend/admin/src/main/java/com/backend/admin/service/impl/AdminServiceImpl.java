package com.backend.admin.service.impl;

import com.backend.admin.entity.AdminEntity;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.backend.admin.service.AdminService;
import com.backend.admin.mapper.AdminMapper;
import com.backend.common.result.Result;
import com.backend.admin.dto.AdminDTO;

/**
 * @author chenyang
 * @date 2026/3/27
 * @description 管理员服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminEntity> implements AdminService {
    private final AdminMapper adminMapper;

}