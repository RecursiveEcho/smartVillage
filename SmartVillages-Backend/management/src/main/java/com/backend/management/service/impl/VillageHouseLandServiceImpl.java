package com.backend.management.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisDistributedLock;
import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.management.dto.VillageHouseLandCreateDTO;
import com.backend.management.dto.VillageHouseLandListPageCache;
import com.backend.management.dto.VillageHouseLandUpdateDTO;
import com.backend.management.entity.VillageHouseLandEntity;
import com.backend.management.mapper.VillageHouseLandMapper;
import com.backend.management.service.VillageHouseLandService;
import com.backend.management.vo.VillageHouseLandDetailVO;
import com.backend.management.vo.VillageHouseLandSimpleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillageHouseLandServiceImpl
        extends ServiceImpl<VillageHouseLandMapper, VillageHouseLandEntity>
        implements VillageHouseLandService {

    private static final String CACHE_KEY_PREFIX = "village_house_land:";
    private static final String CACHE_LIST_VER_KEY = "village-house-land:list:ver";
    private static final String CACHE_LIST_PREFIX = "village-house-land:list:";
    private final RedisJsonCacheTool redisJsonCacheTool;
    private final VillageHouseLandMapper villageHouseLandMapper;
    private final RedisDistributedLock redisDistributedLock;

    /**
     * 创建房屋与土地台账
     * @param villageHouseLandCreateDTO 房屋与土地台账创建DTO
     * @return 房屋与土地台账id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createVillageHouseLand(VillageHouseLandCreateDTO villageHouseLandCreateDTO) {
        VillageHouseLandEntity villageHouseLandEntity = new VillageHouseLandEntity();
        BeanUtils.copyProperties(villageHouseLandCreateDTO, villageHouseLandEntity);
        save(villageHouseLandEntity);
        Integer id = villageHouseLandEntity.getId();
        redisJsonCacheTool.setObject(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id), villageHouseLandEntity);
        bumpListCacheVersion();
        return id;
    }

    /**
     * 分页查询房屋与土地台账列表
     * @param current 当前页
     * @param size 每页条数
     * @param bizType 类型
     * @param ownerName 权利人/户主
     * @param location 坐落
     * @return 房屋与土地台账列表
     */
    @Override
    public IPage<VillageHouseLandSimpleVO> getVillageHouseLandList(Long current, Long size, String bizType, String ownerName, String location) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_PREFIX + CacheKeyUtils.listFilterSegment(bizType, ownerName, location);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        VillageHouseLandListPageCache cached = redisJsonCacheTool.getObject(listKey, VillageHouseLandListPageCache.class);
        if (cached != null) {
            List<VillageHouseLandSimpleVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<VillageHouseLandSimpleVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            return hit;
        }
        LambdaQueryWrapper<VillageHouseLandEntity> queryWrapper = new LambdaQueryWrapper<VillageHouseLandEntity>()
        .eq(StringUtils.hasText(bizType), VillageHouseLandEntity::getBizType, bizType)
        .like(StringUtils.hasText(ownerName), VillageHouseLandEntity::getOwnerName, ownerName)
        .like(StringUtils.hasText(location), VillageHouseLandEntity::getLocation, location)
        .orderByDesc(VillageHouseLandEntity::getCreateTime);
        IPage<VillageHouseLandEntity> entityPage = page(new Page<>(current, size), queryWrapper);
        VillageHouseLandListPageCache toSave = new VillageHouseLandListPageCache();
        toSave.setRecords(entityPage.getRecords().stream().map(this::toHouseLandSimpleVo).toList());
        toSave.setTotal(entityPage.getTotal());
        toSave.setCurrent(entityPage.getCurrent());
        toSave.setSize(entityPage.getSize());
        toSave.setPages(entityPage.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
        return entityPage.convert(this::toHouseLandSimpleVo);
    }

    /**
     * 根据id获取房屋与土地台账详情
     * @param id 房屋与土地台账id
     * @return 房屋与土地台账详情
     */
    @Override
    public VillageHouseLandDetailVO getVillageHouseLandDetail(Integer id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        if(redisJsonCacheTool.isNullMarker(cacheKey)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "房屋与土地台账不存在");
         }
        VillageHouseLandDetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, VillageHouseLandDetailVO.class);
        if (fromCache != null) {
            return fromCache;
        }
        VillageHouseLandEntity entity = villageHouseLandMapper.selectById(id);
        if (entity == null) {
            redisJsonCacheTool.setNullMarker(cacheKey);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "房屋与土地台账不存在");
        }
        VillageHouseLandDetailVO vo = new VillageHouseLandDetailVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        return vo;
    }

    /**
     * 更新房屋与土地台账
     * @param id 房屋与土地台账id
     * @param villageHouseLandUpdateDTO 房屋与土地台账更新DTO
     */
    @Override
    public void update(Integer id, VillageHouseLandUpdateDTO villageHouseLandUpdateDTO) {
        String lockKey = "lock:villageHouseLand:update:" + id;
        String lockInstance = RedisDistributedLock.generateInstanceId();
        boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
        if (!locked) {
            throw new BusinessException(ErrorCode.SYSTEM_BUSY, "房屋与土地台账正在被修改，请稍后再试");
        }
        try {
            VillageHouseLandEntity entity = getById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "房屋与土地台账不存在");
            }
            BeanUtils.copyProperties(villageHouseLandUpdateDTO, entity);
            updateById(entity);
            redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
            bumpListCacheVersion();
        } finally {
            redisDistributedLock.unlock(lockKey, lockInstance);
        }
    }


    /**
     * 删除房屋与土地台账
     * @param id 房屋与土地台账id
     */
    @Override
@Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        String lockKey = "lock:villageHouseLand:delete:" + id;
        String lockInstance = RedisDistributedLock.generateInstanceId();
        boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
        if (!locked) {
            throw new BusinessException(ErrorCode.SYSTEM_BUSY, "房屋与土地台账正在被操作，请稍后再试");
        }
        try {
            VillageHouseLandEntity entity = getById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "房屋与土地台账不存在");
            }
            removeById(id);
            redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
            bumpListCacheVersion();
        } finally {
            redisDistributedLock.unlock(lockKey, lockInstance);
        }
    }


    private VillageHouseLandSimpleVO toHouseLandSimpleVo(VillageHouseLandEntity entity) {
        VillageHouseLandSimpleVO vo = new VillageHouseLandSimpleVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private void bumpListCacheVersion() {
        redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
    }
}
