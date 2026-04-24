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
     * 获取民生服务工单列表
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
            ServiceTicketDetailVO vo = toVo(entity);
            redisJsonCacheTool.setObject(cacheKey, vo);
            return vo;
        }

    @Override
    public void closeMyTicket(Long id, HttpServletRequest request) {

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

