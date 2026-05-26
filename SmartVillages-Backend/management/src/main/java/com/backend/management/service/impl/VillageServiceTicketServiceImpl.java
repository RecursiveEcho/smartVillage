package com.backend.management.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.context.LoginUserContext;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisDistributedLock;
import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.management.dto.ServiceTicketCreateDTO;
import com.backend.management.dto.ServiceTicketDoneDTO;
import com.backend.management.dto.ServiceTicketListPageCache;
import com.backend.management.entity.VillageServiceTicketEntity;
import com.backend.management.mapper.VillageServiceTicketMapper;
import com.backend.management.service.VillageServiceTicketService;
import com.backend.management.vo.ServiceTicketDetailVO;
import com.backend.management.vo.ServiceTicketSimpleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillageServiceTicketServiceImpl
    extends ServiceImpl<VillageServiceTicketMapper, VillageServiceTicketEntity>
    implements VillageServiceTicketService {

  private static final String CACHE_KEY_PREFIX = "village_service_ticket:detail:";
  private static final String CACHE_LIST_VER_KEY = "village-service-ticket:list:ver";
  private static final String CACHE_LIST_MINE_PREFIX = "village-service-ticket:list:mine:";
  private static final String CACHE_LIST_CADRE_PREFIX = "village-service-ticket:list:cadre:";
  private final RedisJsonCacheTool redisJsonCacheTool;
  private final RedisDistributedLock redisDistributedLock;

  /**
   * 创建民生服务工单
   *
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
    entity.setHandlerId(null);
    entity.setHandleNote(null);
    save(entity);
    bumpListCacheVersion();
    return entity.getId();
  }

  /**
   * 获取我的我的民生服务工单列表
   *
   * @param current 当前页
   * @param size 每页条数
   * @param serviceType 服务类型
   * @param status 状态
   * @param request 请求
   * @return 民生服务工单列表
   */
  @Override
  public IPage<ServiceTicketSimpleVO> getServiceTicketList(
      Long current, Long size, String serviceType, Integer status, HttpServletRequest request) {
    Integer applicantId = LoginUserContext.getAuthId(request);
    String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
    String prefix =
        CACHE_LIST_MINE_PREFIX + CacheKeyUtils.listFilterSegment(applicantId, serviceType, status);
    String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
    ServiceTicketListPageCache cached =
        redisJsonCacheTool.getObject(listKey, ServiceTicketListPageCache.class);
    if (cached != null) {
      List<ServiceTicketSimpleVO> rows =
          cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
      Page<ServiceTicketSimpleVO> hit =
          new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
      hit.setRecords(rows);
      return hit;
    }
    LambdaQueryWrapper<VillageServiceTicketEntity> wrapper =
        new LambdaQueryWrapper<VillageServiceTicketEntity>()
            .eq(serviceType != null, VillageServiceTicketEntity::getServiceType, serviceType)
            .eq(status != null, VillageServiceTicketEntity::getStatus, status)
            .eq(applicantId != null, VillageServiceTicketEntity::getApplicantId, applicantId)
            .orderByDesc(VillageServiceTicketEntity::getCreateTime);
    IPage<VillageServiceTicketEntity> entityPage = page(new Page<>(current, size), wrapper);
    ServiceTicketListPageCache toSave = new ServiceTicketListPageCache();
    toSave.setRecords(entityPage.getRecords().stream().map(this::toTicketSimpleVo).toList());
    toSave.setTotal(entityPage.getTotal());
    toSave.setCurrent(entityPage.getCurrent());
    toSave.setSize(entityPage.getSize());
    toSave.setPages(entityPage.getPages());
    redisJsonCacheTool.setListCacheObject(listKey, toSave);
    return entityPage.convert(this::toTicketSimpleVo);
  }

  /**
   * 获取我的民生服务工单详情
   *
   * @param id 民生服务工单id
   * @param request 请求
   * @return 民生服务工单详情
   */
  @Override
  public ServiceTicketDetailVO getMyDetail(Long id, HttpServletRequest request) {
    String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
    Integer applicantId = LoginUserContext.getAuthId(request);
    ServiceTicketDetailVO fromCache =
        redisJsonCacheTool.getObject(
            cacheKey,
            ServiceTicketDetailVO.class,
            () -> {
              VillageServiceTicketEntity entity = getById(id);
              if (entity == null) {
                return null;
              }
              ServiceTicketDetailVO vo = new ServiceTicketDetailVO();
              BeanUtils.copyProperties(entity, vo);
              return vo;
            });
    if (fromCache == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "民生服务工单不存在");
    }
    if (!Objects.equals(fromCache.getApplicantId(), applicantId)) {
      throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此民生服务工单");
    }
    return fromCache;
  }

  /**
   * 取消我的民生服务工单申请
   *
   * @param id 民生服务工单id
   * @param request 请求
   */
  @Override
  public void closeMyTicket(Long id, HttpServletRequest request) {
    VillageServiceTicketEntity entity = getById(id);
    if (!Objects.equals(entity.getApplicantId(), LoginUserContext.getAuthId(request))) {
      throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此民生服务工单");
    }
    if (entity.getStatus() != 0) {
      throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单已处理，无法取消");
    }
    entity.setStatus(3);
    updateById(entity);
    redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
    bumpListCacheVersion();
  }

  /**
   * 管理端获取民生服务工单列表
   *
   * @param current 当前页
   * @param size 每页条数
   * @param serviceType 服务类型
   * @param status 状态
   * @param starTime 开始时间
   * @param endTime 结束时间
   * @return 民生服务工单列表
   */
  @Override
  public IPage<ServiceTicketSimpleVO> pageCadre(
      Long current,
      Long size,
      String serviceType,
      Integer status,
      LocalDateTime starTime,
      LocalDateTime endTime) {
    String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
    String prefix =
        CACHE_LIST_CADRE_PREFIX
            + CacheKeyUtils.listFilterSegment(serviceType, status, starTime, endTime);
    String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
    ServiceTicketListPageCache cached =
        redisJsonCacheTool.getObject(listKey, ServiceTicketListPageCache.class);
    if (cached != null) {
      List<ServiceTicketSimpleVO> rows =
          cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
      Page<ServiceTicketSimpleVO> hit =
          new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
      hit.setRecords(rows);
      return hit;
    }
    LambdaQueryWrapper<VillageServiceTicketEntity> wrapper =
        new LambdaQueryWrapper<VillageServiceTicketEntity>()
            .eq(serviceType != null, VillageServiceTicketEntity::getServiceType, serviceType)
            .eq(status != null, VillageServiceTicketEntity::getStatus, status)
            .ge(starTime != null, VillageServiceTicketEntity::getCreateTime, starTime)
            .le(endTime != null, VillageServiceTicketEntity::getCreateTime, endTime)
            .orderByDesc(VillageServiceTicketEntity::getCreateTime);
    IPage<VillageServiceTicketEntity> entityPage = page(new Page<>(current, size), wrapper);
    ServiceTicketListPageCache toSave = new ServiceTicketListPageCache();
    toSave.setRecords(entityPage.getRecords().stream().map(this::toTicketSimpleVo).toList());
    toSave.setTotal(entityPage.getTotal());
    toSave.setCurrent(entityPage.getCurrent());
    toSave.setSize(entityPage.getSize());
    toSave.setPages(entityPage.getPages());
    redisJsonCacheTool.setListCacheObject(listKey, toSave);
    return entityPage.convert(this::toTicketSimpleVo);
  }

  /**
   * 管理端获取民生服务工单详情
   *
   * @param id 民生服务工单id
   * @return 民生服务工单详情
   */
  @Override
  public ServiceTicketDetailVO getServiceTicketDetail(Long id) {
    String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
    ServiceTicketDetailVO fromCache =
        redisJsonCacheTool.getObject(
            cacheKey,
            ServiceTicketDetailVO.class,
            () -> {
              VillageServiceTicketEntity entity = getById(id);
              if (entity == null) {
                return null;
              }
              ServiceTicketDetailVO vo = new ServiceTicketDetailVO();
              BeanUtils.copyProperties(entity, vo);
              return vo;
            });
    if (fromCache == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "民生服务工单不存在");
    }
    return fromCache;
  }

  /**
   * 管理端处理民生服务工单申请
   *
   * @param id 民生服务工单id
   * @param dto 民生服务工单处理DTO
   * @param request 请求
   */
  @Override
  public void processingServiceTicket(
      Long id, ServiceTicketDoneDTO dto, HttpServletRequest request) {
    String lockKey = "lock:village_service_ticket:processing:" + id;
    String lockInstance = java.util.UUID.randomUUID().toString();
    boolean lockAcquired = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!lockAcquired) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "工单正在处理中，请稍后再试");
    }
    VillageServiceTicketEntity entity = getById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "民生服务工单不存在");
    }
    if (entity.getStatus() == 1) {
      throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单正在处理中，无法处理");
    }
    if (entity.getStatus() == 2) {
      throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单已办结，无法处理");
    }
    if (entity.getStatus() == 3) {
      throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单已关闭，无法处理");
    }
    if (entity.getHandlerId() != null) {
      throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单已有处理人，无法重新处理");
    }
    try{
    entity.setHandlerId(LoginUserContext.getAuthId(request));
    entity.setStatus(1);
    entity.setHandleNote(dto.getHandleNote());
    updateById(entity);
    redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
    bumpListCacheVersion();
    }finally{
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }

  /**
   * 管理端办结民生服务工单申请
   *
   * @param id 民生服务工单id
   * @param request 请求
   */
  @Override
  public void doneServiceTicket(Long id, HttpServletRequest request) {
    String lockKey = "lock:village_service_ticket:done:" + id;
    String lockInstance = java.util.UUID.randomUUID().toString();
    boolean lockAcquired = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!lockAcquired) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "工单正在处理中，请稍后再试");
    }
    VillageServiceTicketEntity entity = getById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "民生服务工单不存在");
    }
    if (entity.getStatus() != 1) {
      throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "民生服务工单正在处理中，无法办结");
    }
    if (!Objects.equals(entity.getHandlerId(), LoginUserContext.getAuthId(request))) {
      throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "您没有权限操作此民生服务工单");
    }
    try {
      entity.setStatus(2);
      updateById(entity);
      redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
      bumpListCacheVersion();
    } finally {
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }

  /**
   * 管理端关闭工单
   *
   * @param id 民生服务工单id
   * @param request 请求
   */
  @Override
  public void closeServiceTicket(Long id, HttpServletRequest request) {
    VillageServiceTicketEntity entity = getById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "民生服务工单不存在");
    }
    if (!Objects.equals(entity.getHandlerId(), LoginUserContext.getAuthId(request))) {
      throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此民生服务工单");
    }
    entity.setStatus(3);
    entity.setHandleNote("管理端关闭工单，申请退回");
    updateById(entity);
    redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
    bumpListCacheVersion();
  }

  /**
   * 后台获取民生服务工单统计
   *
   * @return total: 总申请数 pending: 待处理数 processing: 处理中数 completed: 已办结数
   */
  @Override
  public Map<String, Long> getServiceTicketStatistics() {
    LambdaQueryWrapper<VillageServiceTicketEntity> totalWrapper = new LambdaQueryWrapper<>();
    LambdaQueryWrapper<VillageServiceTicketEntity> pendingWrapper =
        new LambdaQueryWrapper<VillageServiceTicketEntity>()
            .eq(VillageServiceTicketEntity::getStatus, 0);
    LambdaQueryWrapper<VillageServiceTicketEntity> processingWrapper =
        new LambdaQueryWrapper<VillageServiceTicketEntity>()
            .eq(VillageServiceTicketEntity::getStatus, 1);
    LambdaQueryWrapper<VillageServiceTicketEntity> completedWrapper =
        new LambdaQueryWrapper<VillageServiceTicketEntity>()
            .eq(VillageServiceTicketEntity::getStatus, 2);
    return Map.of(
        "total", count(totalWrapper),
        "pending", count(pendingWrapper),
        "processing", count(processingWrapper),
        "completed", count(completedWrapper));
  }

  private ServiceTicketSimpleVO toTicketSimpleVo(VillageServiceTicketEntity entity) {
    ServiceTicketSimpleVO vo = new ServiceTicketSimpleVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }

  private void bumpListCacheVersion() {
    redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
  }
}
