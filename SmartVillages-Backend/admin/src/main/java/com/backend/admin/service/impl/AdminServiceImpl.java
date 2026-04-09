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

import org.springframework.util.StringUtils;

import com.backend.common.exception.BusinessException;
import com.backend.common.enums.ErrorCode;

/**
 * @author chenyang
 * @date 2026/4/8
 * @description 管理员服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminEntity> implements AdminService {
    private final AuthMapper authMapper;

    /**
     * 分页查询用户列表
     *
     * @param current 当前页
     * @param size    每页数量
     * @return 用户列表
     */
    @Override
    public IPage<AdminVO> pageUsers(String username, String role, Integer status, Long current, Long size) {
        Page<AuthEntity> page = new Page<>(current, size);
        LambdaQueryWrapper<AuthEntity> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.hasText(username)){
            wrapper.like(AuthEntity::getUsername, username);
        }
        if(StringUtils.hasText(role)){
            wrapper.eq(AuthEntity::getRole, role);
        }
        if(status != null){
            wrapper.eq(AuthEntity::getStatus, status);
        }
        wrapper.orderByDesc(AuthEntity::getStatus)
                .orderByDesc(AuthEntity::getCreateTime)
                .orderByAsc(AuthEntity::getId);
        IPage<AuthEntity> entityPage = authMapper.selectPage(page, wrapper);
        Page<AdminVO> voPage = new Page<>(
                entityPage.getCurrent(),
                entityPage.getSize(),
                entityPage.getTotal()
        );
        voPage.setRecords(entityPage.getRecords().stream()
                .map(this::convertToAdminVO)
                .collect(Collectors.toList())
        );
        return  voPage;
    }
    private AdminVO convertToAdminVO(AuthEntity entity){
        AdminVO vo = new AdminVO();
        vo.setId(entity.getId());
        vo.setUsername(entity.getUsername());
        vo.setRole(entity.getRole());
        vo.setPhone(entity.getPhone());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        vo.setDeleted(entity.getDeleted());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    @Override
    public void updateUserStatus(Integer id, Integer status) {
        if(id==null){throw new BusinessException(ErrorCode.PARAM_INVALID);}
        if(status==null||status!=0&&status!=1){throw new BusinessException(ErrorCode.STATUS_INVALID);}
        AuthEntity entity = authMapper.selectById(id);
        if(entity == null){throw new BusinessException(ErrorCode.USER_NOT_FOUND);}
        if(Integer.valueOf(1).equals(entity.getDeleted())){throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);}
        AuthEntity updateEntity = new AuthEntity();
        updateEntity.setId(id);
        updateEntity.setStatus(status);
        authMapper.updateById(updateEntity);
    }


}