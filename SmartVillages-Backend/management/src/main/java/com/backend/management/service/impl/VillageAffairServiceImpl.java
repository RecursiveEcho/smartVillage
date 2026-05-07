package com.backend.management.service.impl;

import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VillageAffairServiceImpl
        extends ServiceImpl<VillageAffairMapper, VillageAffairEntity>
        implements VillageAffairService {

    private static final String CACHE_KEY_PREFIX = "village_affair:";
    private static final String CACHE_LIST_VER_KEY = "village-affair:list:ver";
    private static final String CACHE_LIST_ADMIN_PREFIX = "village-affair:list:admin:";
    private static final String CACHE_LIST_PUBLIC_PREFIX = "village-affair:list:public:";
    private final RedisJsonCacheTool redisJsonCacheTool;
    private final VillageAffairMapper villageAffairMapper;
    /**
     * 创建村务事项/公示
     * @param dto 村务事项/公示创建DTO
     * @return 村务事项/公示ID
     */
    @Override
    public Integer create(VillageAffairCreateDTO dto) {
        VillageAffairEntity entity = new VillageAffairEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        Integer id = entity.getId();
        redisJsonCacheTool.setObject(CACHE_KEY_PREFIX, id);
        bumpListCacheVersion();
        return id;
    }

    /**
     * 分页查询村务事项/公示列表
     * @param current 当前页
     * @param size 每页条数
     * @return 村务事项/公示列表
     */
    @Override
    public IPage<VillageAffairSimpleVO> getList(Long current, Long size,Integer status, String affairType, String title) {
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
     * 根据id获取村务事项/公示详情
     * @param id 村务事项/公示id
     * @return 村务事项/公示详情
     */
    @Override
    public VillageAffairDetailVO getDetail(Integer id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        VillageAffairDetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, VillageAffairDetailVO.class);
        if (fromCache != null) {
            return fromCache;
        }
        VillageAffairEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        VillageAffairDetailVO vo = new VillageAffairDetailVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        return vo;
    }

    /**
     * 更新村务事项/公示
     * @param id 村务事项/公示id
     * @param dto 村务事项/公示更新DTO
     */
    @Override
    public void update(Integer id, VillageAffairUpdateDTO dto) {
        VillageAffairEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
        bumpListCacheVersion();
    }

    /**
     * 删除村务事项/公示
     * @param id 村务事项/公示id
     */
    @Override
    public void delete(Integer id) {
        VillageAffairEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        removeById(id);
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
        bumpListCacheVersion();
    }

    /**
     * 审核村务事项/公示
     * @param id 村务事项/公示id
     * @param dto 村务事项/公示审核DTO
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
        updateById(entity);
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
        bumpListCacheVersion();
    }

    /**
     * 前台分页查询村务事项/公示列表
     * @param current 当前页
     * @param size 每页条数
     * @return 村务事项/公示列表
     */
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
        .eq(VillageAffairEntity::getStatus, 2) // 已发布
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

    /**
     * 根据id获取村务事项/公示详情
     * @param id 村务事项/公示id
     * @return 村务事项/公示详情
     */
    @Override
    public VillageAffairDetailVO getPublicDetail(Integer id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        if (redisJsonCacheTool.isNullMarker(cacheKey)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        VillageAffairDetailVO fromCache = tryLoadAndBumpFromCache(id, cacheKey);
        if (fromCache != null) {
            return fromCache;
        }
        VillageAffairEntity entity = villageAffairMapper.selectById(id);
        if (entity == null||entity.getStatus() != 2) {
            redisJsonCacheTool.setNullMarker(cacheKey);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "村务事项/公示不存在");
        }
        VillageAffairDetailVO vo = new VillageAffairDetailVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        return vo;
    }
    
    private VillageAffairDetailVO tryLoadAndBumpFromCache(Integer id, String cacheKey) {
        VillageAffairDetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, VillageAffairDetailVO.class);
        if (fromCache == null) {
            return null;
        }
        VillageAffairEntity entity = villageAffairMapper.selectById(id);
        if (entity == null||entity.getStatus() != 2) {
            redisJsonCacheTool.delete(cacheKey);
            return null;
        }
        return fromCache;
    }

    private VillageAffairSimpleVO toSimpleVo(VillageAffairEntity entity) {
        VillageAffairSimpleVO vo = new VillageAffairSimpleVO();
        BeanUtils.copyProperties(Objects.requireNonNull(entity), vo);
        return vo;
    }

    private void bumpListCacheVersion() {
        redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
    }
}