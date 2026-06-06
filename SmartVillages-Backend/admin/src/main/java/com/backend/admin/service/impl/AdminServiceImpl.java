package com.backend.admin.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.backend.auth.entity.AuthEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import com.backend.admin.client.AuthAdminClient;
import com.backend.common.result.Result;
import com.backend.admin.dto.AuthPublishedPageCache;
import com.backend.admin.entity.AdminEntity;
import com.backend.admin.mapper.AdminMapper;
import com.backend.admin.service.AdminService;
import com.backend.auth.dto.AuthDTO;
import com.backend.auth.vo.AuthVO;
import com.backend.auth.vo.CreateCaderVO;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisDistributedLock;
import com.backend.common.utils.RedisJsonCacheTool;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;

/**
 * 管理员业务实现。
 *
 * <p>继承 MyBatis-Plus 对 {@link AdminEntity} 的基础 CRUD，用户列表与状态变更实际读写 {@link AuthEntity}
 * （认证账号表），与管理员扩展信息分离。
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminEntity>
    implements AdminService {

  private static final String CACHE_KEY_PREFIX = "admin:users:detail:";
  private final RedisDistributedLock redisDistributedLock;
  private final RedisJsonCacheTool redisJsonCacheTool;
  private final AuthAdminClient authAdminClient;

  private static final String CACHE_LIST_KEY_PREFIX = "admin:users:list:";
  private static final String CACHE_LIST_VER_KEY = "admin:users:ver";

  /**
   * 按条件分页查询认证用户，并映射为 {@link AuthVO} 返回给管理端。
   *
   * <p>{@code username} 目前仅占位，未参与查询条件；{@code role}、{@code status} 非空时才会过滤。排序：优先按状态降序、创建时间降序，再按 id
   * 升序，保证列表相对稳定。
   */
  @Override
  public IPage<AuthVO> pageUsers(
    String username, String role, Integer status, Long current, Long size) {

    String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
    String prefix = CACHE_LIST_KEY_PREFIX + CacheKeyUtils.listFilterSegment(username, role, status);
    String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);

    AuthPublishedPageCache cached =redisJsonCacheTool.getObject(listKey, AuthPublishedPageCache.class);

    if (cached != null) {
      List<AuthVO> rows =cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
      Page<AuthVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
      hit.setRecords(rows);
      return hit;
    }

    Result<Page<AuthVO>> result =authAdminClient.pageUsers(username, role, status, current, size);

    Page<AuthVO> page = result.getData();

    if(page == null) {
      page = new Page<>(current, size);
    }

    AuthPublishedPageCache toSave = new AuthPublishedPageCache();
    toSave.setRecords(page.getRecords());
    toSave.setTotal(page.getTotal());
    toSave.setCurrent(page.getCurrent());
    toSave.setSize(page.getSize());
    toSave.setPages(page.getPages());
    redisJsonCacheTool.setListCacheObject(listKey, toSave);

    return page;
  }

  /** 校验用户存在且未逻辑删除后，更新其启用状态。 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateUserStatus(Integer id, Integer status) {
    String lockKey = "lock:admin:user:" + id;
    String lockInstance = RedisDistributedLock.generateInstanceId();
    boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!locked) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "用户正在被修改，请稍后再试");
    }
    try {
      authAdminClient.updateUserStatus(id, status);
      evictDetailCache(id);
      bumpListCacheVersion();
    } finally {
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }

  /**
   * 创建村干部账号。
   *
   * @param authDTO 用户认证 DTO
   * @return 创建后的村干部信息
  */
  @Override
  @SuppressWarnings("null")
  @Transactional(rollbackFor = Exception.class)
  public CreateCaderVO createCadre(AuthDTO authDTO) {
    CreateCaderVO createCaderVO= authAdminClient.createCadre(authDTO).getData();
    bumpListCacheVersion();
    return createCaderVO;
  }

  /**
   * 查看用户详细信息。
   *
   * @param id 用户 ID
   * @return 用户详细信息
   */
  @Override
  public AuthVO getUserDetail(Integer id) {
    String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
    AuthVO fromCache = redisJsonCacheTool.getObject(cacheKey, AuthVO.class,()->{
        AuthVO vo = authAdminClient.getUserDetail(id).getData();
        return vo;
    });
    if(fromCache == null) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
    }
    return fromCache;
  }

  /**
   * 写操作后删除对应详情缓存，避免脏读。
   *
   * @param id 用户 ID
   */
  private void evictDetailCache(Integer id) {
    redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
  }

  private void bumpListCacheVersion() {
    redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
  }
  /**
   * 删除用户。
   *
   * @param id 用户 ID
  */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteUser(Integer id) {
    String lockKey = "lock:admin:user:" + id;
    String lockInstance = RedisDistributedLock.generateInstanceId();
    boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!locked) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "用户正在被操作，请稍后再试");
    }
    try {
      authAdminClient.deleteUser(id);
      evictDetailCache(id);
      redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
    } finally {
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }
}
