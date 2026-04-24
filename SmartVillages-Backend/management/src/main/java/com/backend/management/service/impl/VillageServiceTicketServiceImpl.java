package com.backend.management.service.impl;

import com.backend.common.context.LoginUserContext;
import com.backend.management.dto.ServiceTicketCreateDTO;
import com.backend.management.entity.VillageServiceTicketEntity;
import com.backend.management.mapper.VillageServiceTicketMapper;
import com.backend.management.service.VillageServiceTicketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backend.management.vo.ServiceTicketDetailVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Service
@RequiredArgsConstructor
public class VillageServiceTicketServiceImpl
        extends ServiceImpl<VillageServiceTicketMapper, VillageServiceTicketEntity>
        implements VillageServiceTicketService {

    /**
     * 创建民生服务工单
     * @param dto 民生服务工单创建DTO
     * @param request 请求
     * @return 民生服务工单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer create(ServiceTicketCreateDTO dto, HttpServletRequest request) {
        VillageServiceTicketEntity entity = new VillageServiceTicketEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setApplicantId(LoginUserContext.getAuthId(request));
        save(entity);
        return entity.getId();
    }

    /**
     * 获取民生服务工单列表
     * @param current 当前页
     * @param size 每页条数
     * @param serviceType 服务类型
     * @param status 状态
     * @param request 请求
     * @return 民生服务工单列表
     */
    @Override
    public IPage<ServiceTicketDetailVO> getServiceTicketList(Long current, Long size, String serviceType, Integer status, HttpServletRequest request) {
        LambdaQueryWrapper<VillageServiceTicketEntity> wrapper = new LambdaQueryWrapper<VillageServiceTicketEntity>()
        .eq(serviceType != null, VillageServiceTicketEntity::getServiceType, serviceType)
        .eq(status != null, VillageServiceTicketEntity::getStatus, status)
        .eq(LoginUserContext.getAuthId(request) != null, VillageServiceTicketEntity::getHandlerId, LoginUserContext.getAuthId(request));
        IPage<VillageServiceTicketEntity> entityPage = page(new Page<>(current, size), wrapper);
        return entityPage.convert(this::toVo);
    }

    /**
     * 将实体转换为VO
     * @param entity 实体
     * @return VO
     */
    private ServiceTicketDetailVO toVo(VillageServiceTicketEntity entity) {
        ServiceTicketDetailVO vo = new ServiceTicketDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}

