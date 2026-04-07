package com.backend.admin.service.impl;

import com.backend.admin.entity.AdminEntity;
import com.backend.admin.vo.AdminVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.backend.admin.service.AdminService;
import com.backend.admin.mapper.AdminMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;

import java.util.stream.Collectors;

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
    private final AuthMapper authMapper;

    /**
     * 分页查询用户列表
     *
     * @param current 当前页
     * @param size    每页数量
     * @return 用户列表
     */
    @Override
    public Page<AdminVO> pageUsers(Long current, Long size) {
        Page<AuthEntity> page = new Page<>(current, size);
        LambdaQueryWrapper<AuthEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AuthEntity::getId);

        IPage<AuthEntity> entityPage = authMapper.selectPage(page, wrapper);
        
        Page<AdminVO> voPage = new Page<>(
            entityPage.getCurrent(),
            entityPage.getSize(),
            entityPage.getTotal()
    );
    voPage.setRecords(entityPage.getRecords().stream().map(e -> {
        AdminVO vo = new AdminVO();
        vo.setId(e.getId());
        vo.setUsername(e.getUsername());
        vo.setRole(e.getRole());
        vo.setPhone(e.getPhone());
        vo.setCreateTime(e.getCreateTime());
        vo.setUpdateTime(e.getUpdateTime());
        vo.setDeleted(e.getDeleted());
        vo.setStatus(e.getStatus());
        return vo;
    }).collect(Collectors.toList()));
    return voPage;
    }

    
}