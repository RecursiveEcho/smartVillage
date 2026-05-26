package com.backend.interaction.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.common.context.LoginUserContext;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisDistributedLock;
import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.common.utils.RedisRateLimiter;
import com.backend.interaction.dto.InteractionCreateDTO;
import com.backend.interaction.dto.InteractionPublishedPageCache;
import com.backend.interaction.dto.ReplyInteractionDTO;
import com.backend.interaction.entity.InteractionEntity;
import com.backend.interaction.mapper.InteractionMapper;
import com.backend.interaction.service.InteractionService;
import com.backend.interaction.vo.InteractionCreateVO;
import com.backend.interaction.vo.InteractionDetailVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 村民留言业务实现。
 *
 * <p>负责村民留言的创建、列表、详情、回复、撤回以及 Redis 缓存维护。
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class InteractionServiceImpl extends ServiceImpl<InteractionMapper, InteractionEntity>
    implements InteractionService {

  private final RedisRateLimiter redisRateLimiter;
  private final RedisJsonCacheTool redisJsonCacheTool;
  private final InteractionMapper interactionMapper;
  private final AuthMapper authMapper;
  private final RedisDistributedLock redisDistributedLock;
  private static final String CACHE_KEY_PREFIX = "interaction:detail:";
  private static final String CACHE_LIST_VER_KEY = "interaction:list:ver";

  /** 村民端公开列表（无筛选） */
  private static final String CACHE_LIST_PUB_PREFIX = "interaction:list:pub:";

  /** 管理端带条件列表 */
  private static final String CACHE_LIST_CADRE_PREFIX = "interaction:list:cadre:";

  /** 我的留言（按用户隔离） */
  private static final String CACHE_LIST_MINE_PREFIX = "interaction:list:mine:";

  /** 新增村民留言。 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public InteractionCreateVO createMessage(InteractionCreateDTO dto, HttpServletRequest request) {

    // 限流：每人每分钟最多 5 次创建（根据用户 ID 和 IP 共同限流，防止账号共享导致的滥用）
    String clientIp = request.getRemoteAddr();
    Integer userId = LoginUserContext.getAuthId(request);
    String rateKey = "rate_limit:interaction:create:" + userId + ":" + clientIp;
    redisRateLimiter.check(rateKey, 5);

    InteractionEntity entity = new InteractionEntity();
    entity.setUserId(LoginUserContext.getAuthId(request));
    BeanUtils.copyProperties(dto, entity);
    save(entity);
    InteractionCreateVO vo = new InteractionCreateVO();
    BeanUtils.copyProperties(entity, vo);
    vo.setUsername(LoginUserContext.getUsername(request));
    redisJsonCacheTool.setObject(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, entity.getId()), vo);
    BumpListCacheVersion();
    return vo;
  }

  /** 获取村民留言列表。 */
  @Override
  public IPage<InteractionDetailVO> getMessageList(Long current, Long size) {
    String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
    String prefix = CACHE_LIST_PUB_PREFIX + CacheKeyUtils.listFilterSegment();
    String listCacheKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);

    InteractionPublishedPageCache cached =
        redisJsonCacheTool.getObject(listCacheKey, InteractionPublishedPageCache.class);

    if (cached != null) {
      List<InteractionDetailVO> rows =
          cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();

      enrichUsernames(rows);
      Page<InteractionDetailVO> hit =
          new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
      hit.setRecords(rows);
      return hit;
    }

    LambdaQueryWrapper<InteractionEntity> wrapper =
        new LambdaQueryWrapper<InteractionEntity>().orderByDesc(InteractionEntity::getCreateTime);
    IPage<InteractionEntity> entityPage = page(new Page<>(current, size), wrapper);
    List<InteractionDetailVO> voRows = entityPage.getRecords().stream().map(this::toVo).toList();
    enrichUsernames(voRows);
    InteractionPublishedPageCache toSave = new InteractionPublishedPageCache();
    toSave.setRecords(voRows);
    toSave.setTotal(entityPage.getTotal());
    toSave.setCurrent(entityPage.getCurrent());
    toSave.setSize(entityPage.getSize());
    toSave.setPages(entityPage.getPages());
    redisJsonCacheTool.setListCacheObject(listCacheKey, toSave);
    Page<InteractionDetailVO> out =
        new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
    out.setRecords(voRows);
    return out;
  }

  /** 回复村民留言。 */
  @Override
  public String replyMessage(Long id, ReplyInteractionDTO dto, HttpServletRequest request) {
    InteractionEntity entity = requireById(id);
    if (entity.getStatus() != null && entity.getStatus() == 3) {
      throw new BusinessException(ErrorCode.INTERACTION_CLOSED);
    }
    entity.setReply(dto.getReply());
    entity.setReplyTime(LocalDateTime.now());
    entity.setReplyUser(LoginUserContext.getAuthId(request));
    entity.setStatus(2);
    updateById(entity);
    redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
    BumpListCacheVersion();
    return "回复成功";
  }

  /** 获取村民留言详情。 */
  @Override
  public InteractionDetailVO getMessageDetail(Long id) {
    String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
    InteractionDetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, InteractionDetailVO.class,()->{
      InteractionEntity entity = interactionMapper.selectById(id);
      if (entity == null) {
        return null;
      }
      InteractionDetailVO vo = new InteractionDetailVO();
      BeanUtils.copyProperties(entity, vo);
      return vo;
    });
    if (fromCache== null) {
       throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "留言不存在");
    }
    return fromCache;
  }

  /** 管理端获取村民留言列表。 */
  @Override
  public IPage<InteractionDetailVO> getMessageListByCadre(
      Long current,
      Long size,
      Integer status,
      String type,
      LocalDateTime startTime,
      LocalDateTime endTime) {
    String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
    String prefix =
        CACHE_LIST_CADRE_PREFIX + CacheKeyUtils.listFilterSegment(status, type, startTime, endTime);
    String listCacheKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
    InteractionPublishedPageCache cached =
        redisJsonCacheTool.getObject(listCacheKey, InteractionPublishedPageCache.class);
    if (cached != null) {
      List<InteractionDetailVO> rows =
          cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
      Page<InteractionDetailVO> hit =
          new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
      hit.setRecords(rows);
      return hit;
    }
    LambdaQueryWrapper<InteractionEntity> wrapper =
        new LambdaQueryWrapper<InteractionEntity>()
            .eq(status != null, InteractionEntity::getStatus, status)
            .eq(type != null, InteractionEntity::getType, type)
            .ge(startTime != null, InteractionEntity::getCreateTime, startTime)
            .le(endTime != null, InteractionEntity::getCreateTime, endTime)
            .orderByDesc(InteractionEntity::getCreateTime);
    IPage<InteractionEntity> entityPage = page(new Page<>(current, size), wrapper);
    InteractionPublishedPageCache toSave = new InteractionPublishedPageCache();
    toSave.setRecords(entityPage.getRecords().stream().map(this::toDetailVo).toList());
    toSave.setTotal(entityPage.getTotal());
    toSave.setCurrent(entityPage.getCurrent());
    toSave.setSize(entityPage.getSize());
    toSave.setPages(entityPage.getPages());
    redisJsonCacheTool.setListCacheObject(listCacheKey, toSave);
    return entityPage.convert(this::toDetailVo);
  }

  /** 我的留言。 */
  @Override
  public IPage<InteractionDetailVO> getMyMessageList(
      HttpServletRequest request, Long current, Long size) {
    String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
    Integer userId = LoginUserContext.getAuthId(request);
    String prefix = CACHE_LIST_MINE_PREFIX + CacheKeyUtils.listFilterSegment(userId);
    String listCacheKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
    InteractionPublishedPageCache cached =
        redisJsonCacheTool.getObject(listCacheKey, InteractionPublishedPageCache.class);
    if (cached != null) {
      List<InteractionDetailVO> rows =
          cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
      Page<InteractionDetailVO> hit =
          new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
      hit.setRecords(rows);
      return hit;
    }
    LambdaQueryWrapper<InteractionEntity> wrapper =
        new LambdaQueryWrapper<InteractionEntity>()
            .eq(InteractionEntity::getUserId, LoginUserContext.getAuthId(request))
            .orderByDesc(InteractionEntity::getCreateTime);
    IPage<InteractionEntity> entityPage = page(new Page<>(current, size), wrapper);
    InteractionPublishedPageCache toSave = new InteractionPublishedPageCache();
    toSave.setRecords(entityPage.getRecords().stream().map(this::toDetailVo).toList());
    toSave.setTotal(entityPage.getTotal());
    toSave.setCurrent(entityPage.getCurrent());
    toSave.setSize(entityPage.getSize());
    toSave.setPages(entityPage.getPages());
    redisJsonCacheTool.setListCacheObject(listCacheKey, toSave);
    return entityPage.convert(this::toDetailVo);
  }

  /** 我的留言详细。 */
  @Override
  public InteractionDetailVO getMyMessageDetail(HttpServletRequest request, Long id) {
    String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
    if (redisJsonCacheTool.isNullMarker(cacheKey)) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "留言不存在");
    }
    InteractionDetailVO fromCache =redisJsonCacheTool.getObject(cacheKey, InteractionDetailVO.class,()->{
        InteractionEntity entity = interactionMapper.selectById(id);
        if (entity == null) {
          return null;
        }
        InteractionDetailVO vo = new InteractionDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    });
    if (fromCache == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "留言不存在");
    }
    if(!Objects.equals(fromCache.getUserId(), LoginUserContext.getAuthId(request))) {
      throw new BusinessException(ErrorCode.NO_PERMISSION, "无权访问他人留言");
    }
    return fromCache;
  }


  /** 村民撤回留言。 */
  @Override
  public String withdrawMessage(HttpServletRequest request, Long id) {
    LambdaQueryWrapper<InteractionEntity> wrapper =
        new LambdaQueryWrapper<InteractionEntity>()
            .eq(InteractionEntity::getUserId, LoginUserContext.getAuthId(request))
            .eq(InteractionEntity::getId, id);
    InteractionEntity entity = getOne(wrapper);
    if (entity == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "留言不存在");
    }
    entity.setStatus(3);
    updateById(entity);
    redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
    BumpListCacheVersion();
    return "撤回成功";
  }

  /** 管理端处理村民留言。 */
  @Override
  public String processingMessage(Long id, Integer status, HttpServletRequest request) {
    String lockKey = "lock:interaction:process:" + id;
    String lockInstance = UUID.randomUUID().toString();
    boolean lockAcquired = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!lockAcquired) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "系统繁忙，请稍后再试");
    }

    InteractionEntity entity = requireById(id);
    if (entity.getStatus() != null && entity.getStatus() == 3) {
      throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "留言已关闭，无法处理");
    }
    try {
      entity.setStatus(status);
      updateById(entity);
      redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
      BumpListCacheVersion();
      return "处理成功";
    } finally {
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }

  /** 审核村民留言。 */
  @Override
  public void reviewMessage(Long id, Integer reviewStatus) {
    String lockKey = "lock:interaction:review:" + id;
    String lockInstance = UUID.randomUUID().toString();
    boolean lockAcquired = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!lockAcquired) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "系统繁忙，请稍后再试");
    }
    InteractionEntity entity = requireById(id);
    if (entity.getStatus() != null && entity.getStatus() == 3) {
      throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "留言已关闭，无法审核");
    }
    if (reviewStatus != 0 && reviewStatus != 1 && reviewStatus != 2) {
      throw new BusinessException(ErrorCode.PARAM_INVALID, "无效的审核状态");
    }
    try {
      entity.setStatus(reviewStatus);
      updateById(entity);
      redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
      BumpListCacheVersion();
    } finally {
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }

  /** 获取实体。 */
  private InteractionEntity requireById(Long id) {
    InteractionEntity entity = getById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "留言不存在");
    }
    return entity;
  }

  /** 转换为创建页 VO。 */
  public InteractionDetailVO toVo(InteractionEntity entity) {
    InteractionDetailVO vo = new InteractionDetailVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }

  /** 批量补全公开列表中的留言者用户名（auth 表） */
  private void enrichUsernames(List<InteractionDetailVO> rows) {
    if (rows == null || rows.isEmpty()) {
      return;
    }
    Set<Integer> ids = new HashSet<>();
    for (InteractionDetailVO vo : rows) {
      if (vo.getUserId() != null) ids.add(vo.getUserId());
      if (vo.getReplyUser() != null) ids.add(vo.getReplyUser());
    }
    if (ids.isEmpty()) {
      return;
    }
    List<AuthEntity> auths =
        authMapper.selectList(new LambdaQueryWrapper<AuthEntity>().in(AuthEntity::getId, ids));
    Map<Integer, String> idToName = new HashMap<>();
    for (AuthEntity a : auths) {
      if (a.getId() != null) {
        idToName.put(a.getId(), a.getUsername());
      }
    }
    for (InteractionDetailVO vo : rows) {
      if (vo.getUserId() != null) {
        vo.setUsername(idToName.get(vo.getUserId()));
      }
      if (vo.getReplyUsername() != null) {
        vo.setReplyUsername(idToName.get(vo.getReplyUser()));
      }
    }
  }

  /** 转换为详情页 VO。 */
  public InteractionDetailVO toDetailVo(InteractionEntity entity) {
    InteractionDetailVO vo = new InteractionDetailVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }

  /** 更新列表缓存版本。 */
  public void BumpListCacheVersion() {
    redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
  }
}
