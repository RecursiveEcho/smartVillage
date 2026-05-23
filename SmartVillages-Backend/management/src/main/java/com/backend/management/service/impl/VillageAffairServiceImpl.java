package com.backend.management.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.common.context.LoginUserContext;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisDistributedLock;
import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.management.dto.VillageAffairAuditDTO;
import com.backend.management.dto.VillageAffairCreateDTO;
import com.backend.management.dto.VillageAffairListPageCache;
import com.backend.management.dto.VillageAffairUpdateDTO;
import com.backend.management.entity.VillageAffairEntity;
import com.backend.management.mapper.VillageAffairMapper;
import com.backend.management.service.VillageAffairService;
import com.backend.management.vo.VillageAffairDetailVO;
import com.backend.management.vo.VillageAffairSimpleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillageAffairServiceImpl
        extends ServiceImpl<VillageAffairMapper, VillageAffairEntity>
        implements VillageAffairService {

    private static final String CACHE_ADMIN_DETAIL_KEY_PREFIX = "village_affair:admin:detail:";
    private static final String CACHE_PUBLIC_DETAIL_KEY_PREFIX = "village_affair:public:detail:";
    private static final String CACHE_LIST_VER_KEY = "village-affair:list:ver";
    private static final String CACHE_LIST_ADMIN_PREFIX = "village-affair:list:admin:";
    private static final String CACHE_LIST_PUBLIC_PREFIX = "village-affair:list:public:";
    private final RedisJsonCacheTool redisJsonCacheTool;
    private final VillageAffairMapper villageAffairMapper;
    private final AuthMapper authMapper;
    private final HttpServletRequest request;
    private final RedisDistributedLock redisDistributedLock;
    /**
     * 创建村务事项/公示
     *
     * @param dto 创建参数
     * @return 村务事项/公示ID
     */
    @Override
    public Integer create(VillageAffairCreateDTO dto) {
        VillageAffairEntity entity = new VillageAffairEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateUser(LoginUserContext.getAuthId(request));
        save(entity);
        bumpListCacheVersion();
        return entity.getId();
    }

    /**
     * 获取村务事项/公示列表
     *
     * @param current 页码
     * @param size    每页数量
     * @param status  状态
     * @param affairType 事项类型
     * @param title   标题
     * @return 村务事项/公示列表
     */
    @Override
    public IPage<VillageAffairSimpleVO> getList(Long current, Long size, Integer status, String affairType, String title) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_ADMIN_PREFIX + CacheKeyUtils.listFilterSegment(status, affairType, title);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        VillageAffairListPageCache cached = redisJsonCacheTool.getObject(listKey, VillageAffairListPageCache.class);
        if (cached != null) {
            List<VillageAffairSimpleVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<VillageAffairSimpleVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            return hit;
        }
        LambdaQueryWrapper<VillageAffairEntity> queryWrapper = new LambdaQueryWrapper<VillageAffairEntity>()
        .eq(status != null, VillageAffairEntity::getStatus, status)
        .like(StringUtils.hasText(affairType), VillageAffairEntity::getAffairType, affairType)
        .like(StringUtils.hasText(title), VillageAffairEntity::getTitle, title)
        .orderByDesc(VillageAffairEntity::getCreateTime);
        IPage<VillageAffairEntity> entityPage = page(new Page<>(current, size), queryWrapper);
        VillageAffairListPageCache toSave = new VillageAffairListPageCache();
        toSave.setRecords(entityPage.getRecords().stream().map(this::toSimpleVo).toList());
        toSave.setTotal(entityPage.getTotal());
        toSave.setCurrent(entityPage.getCurrent());
        toSave.setSize(entityPage.getSize());
        toSave.setPages(entityPage.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
        return entityPage.convert(this::toSimpleVo);
    }

    /**
     * 获取村务事项/公示详情
     *
     * @param id 村务事项/公示ID
     * @return 村务事项/公示详情
     */
    @Override
    public VillageAffairDetailVO getDetail(Integer id) {
        String cacheKey = adminDetailCacheKey(id);
        if(redisJsonCacheTool.isNullMarker(cacheKey)){
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        VillageAffairDetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, VillageAffairDetailVO.class);
        if (fromCache != null) {
            enrichUsername(fromCache);
            return fromCache;
        }
        VillageAffairEntity entity = getById(id);
        if (entity == null) {
            redisJsonCacheTool.setNullMarker(cacheKey);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        VillageAffairDetailVO vo = new VillageAffairDetailVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        enrichUsername(vo);
        return vo;
    }

    /**
     * 更新村务事项/公示
     *
     * @param id     村务事项/公示ID
     * @param dto    更新参数
     */
   @Override
public void update(Integer id, VillageAffairUpdateDTO dto) {
    String lockKey = "lock:villageAffair:update:" + id;
    String lockInstance = RedisDistributedLock.generateInstanceId();
    boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!locked) {
        throw new BusinessException(ErrorCode.SYSTEM_BUSY, "村务事项正在被修改，请稍后再试");
    }
    try {
        VillageAffairEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
        evictDetailCaches(id);
        bumpListCacheVersion();
    } finally {
        redisDistributedLock.unlock(lockKey, lockInstance);
    }
}


    /**
     * 删除村务事项/公示
     *
     * @param id 村务事项/公示ID
     */
    @Override
public void delete(Integer id) {
    String lockKey = "lock:villageAffair:delete:" + id;
    String lockInstance = RedisDistributedLock.generateInstanceId();
    boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!locked) {
        throw new BusinessException(ErrorCode.SYSTEM_BUSY, "村务事项正在被操作，请稍后再试");
    }
    try {
        VillageAffairEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        removeById(id);
        evictDetailCaches(id);
        bumpListCacheVersion();
    } finally {
        redisDistributedLock.unlock(lockKey, lockInstance);
    }
}


    /**
     * 审核村务事项/公示
     *
     * @param id     村务事项/公示ID
     * @param dto    审核参数
     */
    @Override
    public void audit(Integer id, VillageAffairAuditDTO dto) {
        VillageAffairEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        if (entity.getStatus() == 2) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "村务事项/公示已发布，不能审核");
        }
        BeanUtils.copyProperties(dto, entity);
        entity.setAuditUserId(LoginUserContext.getAuthId(request));
        entity.setAuditTime(LocalDateTime.now());
        updateById(entity);
        evictDetailCaches(id);
        bumpListCacheVersion();
    }


    @Override
    public IPage<VillageAffairSimpleVO> getPublicList(Long current, Long size, String affairType, String title) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_PUBLIC_PREFIX + CacheKeyUtils.listFilterSegment(affairType, title);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        VillageAffairListPageCache cached = redisJsonCacheTool.getObject(listKey, VillageAffairListPageCache.class);
        if (cached != null) {
            List<VillageAffairSimpleVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<VillageAffairSimpleVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            return hit;
        }
        LambdaQueryWrapper<VillageAffairEntity> queryWrapper = new LambdaQueryWrapper<VillageAffairEntity>()
        .eq(VillageAffairEntity::getStatus, 2)
        .like(StringUtils.hasText(affairType), VillageAffairEntity::getAffairType, affairType)
        .like(StringUtils.hasText(title), VillageAffairEntity::getTitle, title)
        .orderByDesc(VillageAffairEntity::getPublishTime);
        IPage<VillageAffairEntity> entityPage = page(new Page<>(current, size), queryWrapper);
        VillageAffairListPageCache toSave = new VillageAffairListPageCache();
        toSave.setRecords(entityPage.getRecords().stream().map(this::toSimpleVo).toList());
        toSave.setTotal(entityPage.getTotal());
        toSave.setCurrent(entityPage.getCurrent());
        toSave.setSize(entityPage.getSize());
        toSave.setPages(entityPage.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
        return entityPage.convert(this::toSimpleVo);
    }

    @Override
    public VillageAffairDetailVO getPublicDetail(Integer id) {
        String cacheKey = publicDetailCacheKey(id);
        if (redisJsonCacheTool.isNullMarker(cacheKey)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        VillageAffairDetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, VillageAffairDetailVO.class);
        if (fromCache != null) {
            enrichUsername(fromCache);
            return fromCache;
        }
        VillageAffairEntity entity = villageAffairMapper.selectById(id);
        if (entity == null || entity.getStatus() != 2) {
            redisJsonCacheTool.setNullMarker(cacheKey);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        VillageAffairDetailVO vo = new VillageAffairDetailVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        enrichUsername(vo);
        return vo;
    }

    private VillageAffairSimpleVO toSimpleVo(VillageAffairEntity entity) {
        VillageAffairSimpleVO vo = new VillageAffairSimpleVO();
        BeanUtils.copyProperties(Objects.requireNonNull(entity), vo);
        return vo;
    }

    private void bumpListCacheVersion() {
        redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
    }

    private void evictDetailCaches(Integer id) {
        redisJsonCacheTool.delete(adminDetailCacheKey(id));
        redisJsonCacheTool.delete(publicDetailCacheKey(id));
    }

    private String adminDetailCacheKey(Integer id) {
        return CacheKeyUtils.detailKey(CACHE_ADMIN_DETAIL_KEY_PREFIX, id);
    }

    private String publicDetailCacheKey(Integer id) {
        return CacheKeyUtils.detailKey(CACHE_PUBLIC_DETAIL_KEY_PREFIX, id);
    }

    private void enrichUsernames(List<VillageAffairDetailVO> rows) {
        if (rows == null || rows.isEmpty()) return;
        Set<Integer> ids = new HashSet<>();
        for (VillageAffairDetailVO vo : rows) {
            if (vo.getCreateUser() != null) ids.add(vo.getCreateUser());
            if (vo.getAuditUserId() != null) ids.add(vo.getAuditUserId());
        }
        if (ids.isEmpty()) return;
        List<AuthEntity> auths = authMapper.selectList(
                new LambdaQueryWrapper<AuthEntity>().in(AuthEntity::getId, ids));
        Map<Integer, String> idToName = new HashMap<>();
        for (AuthEntity a : auths) {
            if (a.getId() != null) idToName.put(a.getId(), a.getUsername());
        }
        for (VillageAffairDetailVO vo : rows) {
            if (vo.getCreateUser() != null) vo.setCreateUserName(idToName.get(vo.getCreateUser()));
            if (vo.getAuditUserId() != null) vo.setAuditUserName(idToName.get(vo.getAuditUserId()));
        }
    }

    private void enrichUsername(VillageAffairDetailVO vo) {
        if (vo == null) return;
        enrichUsernames(Collections.singletonList(vo));
    }
}
