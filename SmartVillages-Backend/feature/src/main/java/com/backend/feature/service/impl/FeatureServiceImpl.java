package com.backend.feature.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.backend.common.aop.OperationLog;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.common.context.LoginUserContext;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisDistributedLock;
import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.feature.dto.FeaturePublishedPageCache;
import com.backend.feature.dto.HighlightCreateDTO;
import com.backend.feature.entity.FeatureEntity;
import com.backend.feature.mapper.FeatureMapper;
import com.backend.feature.service.FeatureService;
import com.backend.feature.vo.FeatureVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 乡村风采服务实现类。
 *
 * <p>负责乡村风采的创建、查询、详情浏览量、上下架、统计以及 Redis 缓存维护。
 */
@Service
@RequiredArgsConstructor
public class FeatureServiceImpl extends ServiceImpl<FeatureMapper, FeatureEntity>
    implements FeatureService {

  private static final String CACHE_KEY_PREFIX = "feature:detail:";
  private static final String CACHE_LIST_KEY_PREFIX = "feature:list:";
  private static final String CACHE_LIST_VER_KEY = "feature:list:ver";
  private static final String CACHE_LIST_ADMIN_PREFIX = "feature:list:admin:";
  private static final String CACHE_LIST_ADMIN_VER_KEY = "feature:list:admin:ver";

  private final RedisDistributedLock redisDistributedLock;
  private final AuthMapper authMapper;
  private final RedisJsonCacheTool redisJsonCacheTool;
  private final FeatureMapper featureMapper;
  private final ObjectMapper objectMapper;

  /** 创建乡村风采。 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void createFeature(HighlightCreateDTO dto, HttpServletRequest request) {
    FeatureEntity entity = new FeatureEntity();
    entity.setTitle(dto.getTitle());
    entity.setContent(dto.getContent());
    entity.setType(dto.getType());
    entity.setCover(dto.getCover());
    if (dto.getVideo() != null) {
      entity.setVideo(dto.getVideo());
    }
    if (dto.getImages() != null) {
      entity.setImages(dto.getImages());
    }
    entity.setCreateUser(LoginUserContext.getAuthId(request));
    save(entity);
    bumpPublishedListCacheVersion();
    bumpAdminListCacheVersion();
  }

  /** 村民获取乡村风采列表。 */
  @Override
  public IPage<FeatureVO> getFeatureList(
      Long current,
      Long size,
      String type,
      Integer getSort,
      LocalDateTime getCreateTime,
      LocalDateTime startTime,
      LocalDateTime endTime,
      HttpServletRequest request) {
    String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
    String prefix =
        CACHE_LIST_KEY_PREFIX
            + CacheKeyUtils.listFilterSegment(type, getSort, getCreateTime, startTime, endTime);
    String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
    FeaturePublishedPageCache cached =
        redisJsonCacheTool.getObject(listKey, FeaturePublishedPageCache.class);
    if (cached != null) {
      List<FeatureVO> rows =
          cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
      Page<FeatureVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
      hit.setRecords(rows);
      enrivchUsernames(rows);
      return hit;
    }
    LambdaQueryWrapper<FeatureEntity> wrapper =
        new LambdaQueryWrapper<FeatureEntity>()
            .eq(FeatureEntity::getStatus, 1)
            .eq(FeatureEntity::getDeleted, 0)
            .eq(type != null, FeatureEntity::getType, type)
            .ge(startTime != null, FeatureEntity::getCreateTime, startTime)
            .le(endTime != null, FeatureEntity::getCreateTime, endTime)
            .orderByDesc(getSort != null, FeatureEntity::getSort, FeatureEntity::getCreateTime);
    IPage<FeatureEntity> entityPage = page(new Page<>(current, size), wrapper);
    FeaturePublishedPageCache toSave = new FeaturePublishedPageCache();
    toSave.setRecords(entityPage.getRecords().stream().map(this::toVo).toList());
    toSave.setTotal(entityPage.getTotal());
    toSave.setCurrent(entityPage.getCurrent());
    toSave.setSize(entityPage.getSize());
    toSave.setPages(entityPage.getPages());
    redisJsonCacheTool.setListCacheObject(listKey, toSave);
    IPage<FeatureVO> result = entityPage.convert(this::toVo);
    enrivchUsernames(result.getRecords());
    return result;
  }

  /** 获取乡村风采详情。 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public FeatureVO getFeatureDetail(Long id) {
    String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
    FeatureVO fromCache = redisJsonCacheTool.getObject(cacheKey, FeatureVO.class,()->{
        FeatureEntity entity = getById(id);
        if (entity == null || !Objects.equals(entity.getStatus(), 1)) {
          return null;
        }
        return toVo(entity);
    });
    if (fromCache == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
    }
      doViewCountBump(id, cacheKey, fromCache);
      enrivchUsername(fromCache);
      return fromCache;
  }
   private void doViewCountBump(Long id, String cacheKey, FeatureVO vo) {
  LambdaUpdateWrapper<FeatureEntity> bump =
      new LambdaUpdateWrapper<FeatureEntity>()
          .eq(FeatureEntity::getId, id)
          .eq(FeatureEntity::getStatus, 1)
          .setSql("view_count = IFNULL(view_count, 0) + 1");
  int rows = featureMapper.update(null, bump);
  if (rows <= 0) {
    redisJsonCacheTool.delete(cacheKey);
    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
  }
  int current = vo.getViewCount() == null ? 0 : vo.getViewCount();
  vo.setViewCount(current + 1);
  redisJsonCacheTool.setObject(cacheKey, vo);
}


  /** 上下架乡村风采。 */
  @Override
  @OperationLog("更新乡村风采状态")
  @Transactional(rollbackFor = Exception.class)
  public void updateStatus(Long id, Integer status, HttpServletRequest request) {
    String lockKey = "lock:status:feature:" + id;
    String lockInstance = RedisDistributedLock.generateInstanceId();
    boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!locked) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "乡村风采正在被修改，请稍后再试");
    }
    try {
      FeatureEntity entity = getById(id);
      if (entity == null) {
        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
      }
      if (!Objects.equals(entity.getCreateUser(), LoginUserContext.getAuthId(request))) {
        throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此乡村风采");
      }
      entity.setStatus(status);
      updateById(entity);
      evictDetailCache(id);
      bumpPublishedListCacheVersion();
      bumpAdminListCacheVersion();
    } finally {
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }

  /** 管理端获取乡村风采列表。 */
  @Override
  public IPage<FeatureVO> getFeatureListByAdmin(
      Long current,
      Long size,
      Integer status,
      String title,
      String type,
      Integer getSort,
      LocalDateTime getCreateTime,
      LocalDateTime startTime,
      LocalDateTime endTime,
      HttpServletRequest request) {
    Integer uid = LoginUserContext.getAuthId(request);
    String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_ADMIN_VER_KEY);
    String prefix =
        CACHE_LIST_ADMIN_PREFIX
            + CacheKeyUtils.listFilterSegment(
                uid, status, title, type, getSort, getCreateTime, startTime, endTime);
    String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
    FeaturePublishedPageCache cached =
        redisJsonCacheTool.getObject(listKey, FeaturePublishedPageCache.class);
    if (cached != null) {
      List<FeatureVO> rows =
          cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
      Page<FeatureVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
      hit.setRecords(rows);
      enrivchUsernames(rows);
      return hit;
    }
    LambdaQueryWrapper<FeatureEntity> wrapper =
        new LambdaQueryWrapper<FeatureEntity>()
            .eq(FeatureEntity::getCreateUser, uid)
            .eq(FeatureEntity::getDeleted, 0)
            .like(StringUtils.hasText(title), FeatureEntity::getTitle, title)
            .eq(type != null, FeatureEntity::getType, type)
            .ge(startTime != null, FeatureEntity::getCreateTime, startTime)
            .le(endTime != null, FeatureEntity::getCreateTime, endTime)
            .eq(status != null, FeatureEntity::getStatus, status)
            .orderByDesc(getSort != null, FeatureEntity::getSort)
            .orderByDesc(getCreateTime != null, FeatureEntity::getCreateTime);
    IPage<FeatureEntity> entityPage = page(new Page<>(current, size), wrapper);
    FeaturePublishedPageCache toSave = new FeaturePublishedPageCache();
    toSave.setRecords(entityPage.getRecords().stream().map(this::toVo).toList());
    toSave.setTotal(entityPage.getTotal());
    toSave.setCurrent(entityPage.getCurrent());
    toSave.setSize(entityPage.getSize());
    toSave.setPages(entityPage.getPages());
    redisJsonCacheTool.setListCacheObject(listKey, toSave);
    IPage<FeatureVO> result = entityPage.convert(this::toVo);
    enrivchUsernames(result.getRecords());
    return result;
  }

  // 绑定上传后的媒体 URL 到乡村风采记录
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void bindUploadedMedia(
      Long featureId,
      String slot,
      String mediaUrl,
      String uploadedFileType,
      Integer operatorUserId) {
    String lockKey = "lock:bindUpload:" + featureId;
    String lockInstance = RedisDistributedLock.generateInstanceId();
    boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!locked) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "上传功能业务繁忙");
    }
    if (featureId == null) {
      throw new BusinessException(ErrorCode.PARAM_INVALID, "风采 ID 不能为空");
    }
    if (!StringUtils.hasText(mediaUrl)) {
      throw new BusinessException(ErrorCode.PARAM_INVALID, "媒体 URL 不能为空");
    }
    String s = slot == null ? "" : slot.trim().toUpperCase();
    FeatureEntity entity = getById(featureId);
    if (entity == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
    }
    if (!Objects.equals(entity.getCreateUser(), operatorUserId)) {
      throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此乡村风采");
    }
    try {
      String ft = uploadedFileType == null ? "" : uploadedFileType.trim().toLowerCase();
      switch (s) {
        case "COVER" -> {
          if (!"image".equals(ft)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "封面绑定仅支持图片（fileType=image）");
          }
          entity.setCover(mediaUrl);
        }
        case "VIDEO" -> {
          if (!"video".equals(ft)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "视频绑定仅支持视频（fileType=video）");
          }
          entity.setVideo(mediaUrl);
        }
        case "IMAGES_APPEND" -> {
          if (!"image".equals(ft)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "相册追加仅支持图片（fileType=image）");
          }
          entity.setImages(appendImagesJson(entity.getImages(), mediaUrl));
        }
        default ->
            throw new BusinessException(
                ErrorCode.PARAM_INVALID, "bindSlot 须为 COVER、VIDEO 或 IMAGES_APPEND");
      }
      updateById(entity);
      evictDetailCache(featureId);
      bumpPublishedListCacheVersion();
      bumpAdminListCacheVersion();
    } finally {
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }

  private String appendImagesJson(String existing, String newUrl) {
    List<String> urls = new ArrayList<>();
    if (StringUtils.hasText(existing)) {
      String trimmed = existing.trim();
      try {
        urls.addAll(objectMapper.readValue(trimmed, new TypeReference<List<String>>() {}));
      } catch (JsonProcessingException e) {
        urls.add(trimmed);
      }
    }
    urls.add(newUrl);
    try {
      return objectMapper.writeValueAsString(urls);
    } catch (JsonProcessingException e) {
      throw new BusinessException(ErrorCode.PARAM_INVALID, "图片列表序列化失败");
    }
  }

  /** 修改乡村风采。 */
  @Override
  @OperationLog("更新乡村风采")
  @Transactional(rollbackFor = Exception.class)
  public void updateFeature(Long id, HighlightCreateDTO dto, HttpServletRequest request) {
    String lockKey = "lock:update:feature:" + id;
    String lockInstance = RedisDistributedLock.generateInstanceId();
    boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!locked) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "乡村风采正在被修改，请稍后再试");
    }
    try {
      FeatureEntity entity = getById(id);
      if (entity == null) {
        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
      }
      if (!Objects.equals(entity.getCreateUser(), LoginUserContext.getAuthId(request))) {
        throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此乡村风采");
      }
      entity.setTitle(dto.getTitle());
      entity.setContent(dto.getContent());
      entity.setType(dto.getType());
      entity.setCover(dto.getCover());
      if (dto.getVideo() != null) {
        entity.setVideo(dto.getVideo());
      }
      if (dto.getImages() != null) {
        entity.setImages(dto.getImages());
      }
      updateById(entity);
      evictDetailCache(id);
      bumpPublishedListCacheVersion();
      bumpAdminListCacheVersion();
    } finally {
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }

  /** 删除乡村风采。 */
  @Override
  @OperationLog("删除乡村风采")
  @Transactional(rollbackFor = Exception.class)
  public void deleteFeature(Long id, HttpServletRequest request) {
    String lockKey = "lock:delete:feature:" + id;
    String lockInstance = RedisDistributedLock.generateInstanceId();
    boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!locked) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "乡村风采正在被操作，请稍后再试");
    }
    try {
      FeatureEntity entity = getById(id);
      if (entity == null) {
        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
      }
      if (!Objects.equals(entity.getCreateUser(), LoginUserContext.getAuthId(request))) {
        throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此乡村风采");
      }
      removeById(id);
      evictDetailCache(id);
      bumpPublishedListCacheVersion();
      bumpAdminListCacheVersion();
    } finally {
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }

  /** 类型统计。 */
  @Override
  public Map<String, Long> getFeatureTypeStatistics() {
    String[] types = {"scenery", "product", "culture", "history"};
    Map<String, Long> result = new HashMap<>(types.length);

    for (String t : types) {
      long total =
          lambdaQuery()
              .eq(FeatureEntity::getDeleted, 0)
              .eq(FeatureEntity::getStatus, 1)
              .eq(FeatureEntity::getType, t)
              .count();
      result.put(t, total);
    }
    return result;
  }

  /** 获取我的乡村风采数量。 */
  @Override
  public Map<String, Long> getMyFeatureCount(HttpServletRequest request) {
    LambdaQueryWrapper<FeatureEntity> wrapper =
        new LambdaQueryWrapper<FeatureEntity>()
            .eq(FeatureEntity::getCreateUser, LoginUserContext.getAuthId(request));
    LambdaQueryWrapper<FeatureEntity> wrapper2 =
        new LambdaQueryWrapper<FeatureEntity>()
            .eq(FeatureEntity::getCreateUser, LoginUserContext.getAuthId(request))
            .eq(FeatureEntity::getStatus, 0);
    LambdaQueryWrapper<FeatureEntity> wrapper3 =
        new LambdaQueryWrapper<FeatureEntity>()
            .eq(FeatureEntity::getCreateUser, LoginUserContext.getAuthId(request))
            .eq(FeatureEntity::getStatus, 1);
    Map<String, Long> result = new HashMap<>();
    result.put("total", count(wrapper));
    result.put("In reality", count(wrapper3));
    result.put("Hidden", count(wrapper2));
    return result;
  }

  /** 乡村风采审核。 */
  @Override
  @OperationLog("审核乡村风采")
  @Transactional(rollbackFor = Exception.class)
  public void reviewFeature(Long id, Integer reviewStatus) {
    String lockKey = "lock:review:feature:" + id;
    String lockInstance = RedisDistributedLock.generateInstanceId();
    boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!locked) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "乡村风采正在被审核，请稍后再试");
    }
    FeatureEntity entity = getById(id);
    // 如果实体不存在，则抛出异常
    if (entity == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
    }
    try {
      entity.setStatus(reviewStatus);
      updateById(entity);
      evictDetailCache(id);
      bumpPublishedListCacheVersion();
      bumpAdminListCacheVersion();
    } finally {
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }

  /** 使所有已发布列表分页缓存失效：版本号 +1，读侧使用新前缀。 */
  public void bumpPublishedListCacheVersion() {
    redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
  }

  private void bumpAdminListCacheVersion() {
    redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_ADMIN_VER_KEY);
  }

  /** 写操作后删除对应详情缓存，避免脏读。 */
  private void evictDetailCache(Long id) {
    redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
  }

  /** 转换为 VO。 */
  private FeatureVO toVo(FeatureEntity entity) {
    FeatureVO vo = new FeatureVO();
    BeanUtils.copyProperties(Objects.requireNonNull(entity), vo);
    return vo;
  }

  private void enrivchUsernames(List<FeatureVO> rows) {
    if (rows == null || rows.isEmpty()) {
      return;
    }
    Set<Integer> ids = new HashSet<>();
    for (FeatureVO vo : rows) {
      if (vo.getCreateUser() != null) {
        ids.add(vo.getCreateUser());
      }
    }
    if (ids.isEmpty()) {
      return;
    }
    List<AuthEntity> auths =
        authMapper.selectList(new LambdaQueryWrapper<AuthEntity>().in(AuthEntity::getId, ids));
    Map<Integer, String> idNoName = new HashMap<>();
    for (AuthEntity auth : auths) {
      if (auth.getId() != null) {
        idNoName.put(auth.getId(), auth.getUsername());
      }
    }
    for (FeatureVO vo : rows) {
      if (vo.getCreateUser() != null) {
        vo.setCreateUserName(idNoName.get(vo.getCreateUser()));
      }
    }
  }

  private void enrivchUsername(FeatureVO vo) {
    if (vo == null) {
      return;
    }
    enrivchUsernames(Collections.singletonList(vo));
  }
}
