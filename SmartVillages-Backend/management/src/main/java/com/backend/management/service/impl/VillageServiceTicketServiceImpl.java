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
import com.backend.management.vo.ServiceTicketSimpleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.common.exception.BusinessException;
import com.backend.common.enums.ErrorCode;
import com.backend.common.utils.CacheKeyUtils;
import java.util.Objects;
import java.time.LocalDateTime;
import com.backend.management.dto.ServiceTicketDoneDTO;
@Service
@RequiredArgsConstructor
public class VillageServiceTicketServiceImpl
        extends ServiceImpl<VillageServiceTicketMapper, VillageServiceTicketEntity>
        implements VillageServiceTicketService {

    private static final String CACHE_KEY_PREFIX = "village_service_ticket:detail:";
    private final RedisJsonCacheTool redisJsonCacheTool;

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
     * 获取我的我的民生服务工单列表
     * @param current 当前页
     * @param size 每页条数
     * @param serviceType 服务类型
     * @param status 状态
     * @param request 请求
     * @return 民生服务工单列表
     */
    @Override
    public IPage<ServiceTicketSimpleVO> getServiceTicketList(Long current, Long size, String serviceType, Integer status, HttpServletRequest request) {
        LambdaQueryWrapper<VillageServiceTicketEntity> wrapper = new LambdaQueryWrapper<VillageServiceTicketEntity>()
        .eq(serviceType != null, VillageServiceTicketEntity::getServiceType, serviceType)
        .eq(status != null, VillageServiceTicketEntity::getStatus, status)
        .eq(LoginUserContext.getAuthId(request) != null, VillageServiceTicketEntity::getApplicantId, LoginUserContext.getAuthId(request));
        IPage<VillageServiceTicketEntity> entityPage = page(new Page<>(current, size), wrapper);
        return entityPage.convert(entity -> {
            ServiceTicketSimpleVO vo = new ServiceTicketSimpleVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }
    /**
     * 获取我的民生服务工单详情
     * @param id 民生服务工单id
     * @param request 请求
     * @return 民生服务工单详情
     */
    @Override
            public ServiceTicketDetailVO getMyDetail(Long id, HttpServletRequest request) {
            String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
            ServiceTicketDetailVO fromCache =redisJsonCacheTool.getObject(cacheKey,ServiceTicketDetailVO.class);
            if (fromCache != null) {
                return fromCache;
            }
            VillageServiceTicketEntity entity = requireById(id);
            if(!Objects.equals(entity.getApplicantId(), LoginUserContext.getAuthId(request))) {
                throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此民生服务工单");
            }
            ServiceTicketDetailVO vo = new ServiceTicketDetailVO();
            BeanUtils.copyProperties(entity, vo);
            redisJsonCacheTool.setObject(cacheKey, vo);
            return vo;
        }

    /**
     * 取消我的民生服务工单申请
     * @param id 民生服务工单id
     * @param request 请求
     */
    @Override
        public void closeMyTicket(Long id, HttpServletRequest request) {
            VillageServiceTicketEntity entity = requireById(id);
            if(!Objects.equals(entity.getApplicantId(), LoginUserContext.getAuthId(request))) {
                throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此民生服务工单");
            }
            if(entity.getStatus() != 0) {
                throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单已处理，无法取消");
            }
            entity.setStatus(3);
            updateById(entity);
        }


    /**
     * 管理端获取民生服务工单列表
     * @param current 当前页
     * @param size 每页条数
     * @param serviceType 服务类型
     * @param status 状态
     * @param starTime 开始时间
     * @param endTime 结束时间
     * @return 民生服务工单列表
     */
    @Override
    public IPage<ServiceTicketSimpleVO> pageCadre(Long current,Long size,String serviceType,Integer status,LocalDateTime starTime,LocalDateTime endTime) {
        LambdaQueryWrapper<VillageServiceTicketEntity> wrapper = new LambdaQueryWrapper<VillageServiceTicketEntity>()
        .eq(serviceType != null, VillageServiceTicketEntity::getServiceType, serviceType)
        .eq(status != null, VillageServiceTicketEntity::getStatus, status)
        .ge(starTime != null, VillageServiceTicketEntity::getCreateTime, starTime)
        .le(endTime != null, VillageServiceTicketEntity::getCreateTime, endTime)
        .orderByDesc(VillageServiceTicketEntity::getCreateTime);
        IPage<VillageServiceTicketEntity> entityPage = page(new Page<>(current, size), wrapper);
        return entityPage.convert(entity -> {
            ServiceTicketSimpleVO vo = new ServiceTicketSimpleVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }

    /**
     * 管理端获取民生服务工单详情
     * @param id 民生服务工单id
     * @return 民生服务工单详情
     */
    @Override
    public ServiceTicketDetailVO getServiceTicketDetail(Long id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        ServiceTicketDetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, ServiceTicketDetailVO.class);
        if (fromCache != null) {
            return fromCache;
        }
        VillageServiceTicketEntity entity = requireById(id);
        ServiceTicketDetailVO vo = new ServiceTicketDetailVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        return vo;
    }

    /**
     * 管理端处理民生服务工单申请
     * @param id 民生服务工单id
     * @param dto 民生服务工单处理DTO
     * @param request 请求
     */
    @Override
    public void processingServiceTicket(Long id, ServiceTicketDoneDTO dto, HttpServletRequest request) {
        VillageServiceTicketEntity entity = requireById(id);
        if(entity.getStatus() == 1) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单正在处理中，无法处理");
        }
        if(entity.getStatus() == 2) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单已办结，无法处理");
        }
        if(entity.getStatus() == 3) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单已关闭，无法处理");
        }
        if(entity.getHandlerId() != null) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单已有处理人，无法重新处理");
        }
        entity.setHandlerId(LoginUserContext.getAuthId(request));
        entity.setStatus(1);
        entity.setHandleNote(dto.getHandleNote());
        updateById(entity);
    }

    /**
     * 管理端办结民生服务工单申请
     * @param id 民生服务工单id
     * @param request 请求
     */
    @Override
    public void doneServiceTicket(Long id, HttpServletRequest request) {
        VillageServiceTicketEntity entity = requireById(id);
        if(entity.getStatus() != 1) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单正在处理中，无法办结");
        }
        if(Objects.equals(entity.getHandlerId(),LoginUserContext.getAuthId(request))) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "您没有权限操作此民生服务工单");
        }
        entity.setStatus(2);
        updateById(entity);
    }

    /**
     * 管理端关闭工单
     * @param id 民生服务工单id
     * @param request 请求
     */
    @Override
    public void closeServiceTicket(Long id, HttpServletRequest request) {
        VillageServiceTicketEntity entity = requireById(id);
        if(!Objects.equals(entity.getHandlerId(),LoginUserContext.getAuthId(request))) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此民生服务工单");
        }
        entity.setStatus(3);
        entity.setHandleNote("管理端关闭工单，申请退回");
        updateById(entity);
    }
    /**
     * 获取实体并校验是否存在
     * @param id 民生服务工单id
     * @return 实体
     */
    private VillageServiceTicketEntity requireById(Long id) {
        VillageServiceTicketEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "民生服务工单不存在");
        }
        return entity;
    }

    
}

