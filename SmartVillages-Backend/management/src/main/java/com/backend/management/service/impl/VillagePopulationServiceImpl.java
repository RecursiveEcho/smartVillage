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
import com.backend.management.dto.VillagePopulationCreateDTO;
import com.backend.management.dto.VillagePopulationListPageCache;
import com.backend.management.dto.VillagePopulationUpdateDTO;
import com.backend.management.entity.VillagePopulationEntity;
import com.backend.management.mapper.VillagePopulationMapper;
import com.backend.management.service.VillagePopulationService;
import com.backend.management.vo.VillagePopulationDetailVO;
import com.backend.management.vo.VillagePopulationSimpleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VillagePopulationServiceImpl
        extends ServiceImpl<VillagePopulationMapper, VillagePopulationEntity>
        implements VillagePopulationService {

    private static final String CACHE_KEY_PREFIX = "village_population:";
    private static final String CACHE_LIST_VER_KEY = "village-population:list:ver";
    private static final String CACHE_LIST_PREFIX = "village-population:list:";
    private final RedisJsonCacheTool redisJsonCacheTool;
    private final RedisDistributedLock redisDistributedLock;
    /**
     * 创建人口台账
     * @param villagePopulationCreateDTO 人口台账创建DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createVillagePopulation(VillagePopulationCreateDTO villagePopulationCreateDTO) {
        VillagePopulationEntity villagePopulationEntity = new VillagePopulationEntity();
        BeanUtils.copyProperties(villagePopulationCreateDTO, villagePopulationEntity);
        save(villagePopulationEntity);
        redisJsonCacheTool.setObject(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, villagePopulationEntity.getId()), villagePopulationEntity);
        bumpListCacheVersion();
    }

    /**
     * 分页查询人口台账列表
     * @param current 当前页
     * @param size 每页条数
     * @param householdNo 户号
     * @param fullName 姓名
     * @param gender 性别
     * @param relationToHead 与户主关系
     * @return 分页查询结果
     */
    @Override
    public IPage<VillagePopulationSimpleVO> getVillagePopulationList(Long current, Long size, String householdNo, String fullName, Integer gender, String relationToHead) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_PREFIX + CacheKeyUtils.listFilterSegment(householdNo, fullName, gender, relationToHead);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        VillagePopulationListPageCache cached = redisJsonCacheTool.getObject(listKey, VillagePopulationListPageCache.class);
        if (cached != null) {
            List<VillagePopulationSimpleVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<VillagePopulationSimpleVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            return hit;
        }
        LambdaQueryWrapper<VillagePopulationEntity> queryWrapper = new LambdaQueryWrapper<VillagePopulationEntity>()
        .like(StringUtils.hasText(householdNo), VillagePopulationEntity::getHouseholdNo, householdNo)
        .like(StringUtils.hasText(fullName), VillagePopulationEntity::getFullName, fullName)
        .eq(gender != null, VillagePopulationEntity::getGender, gender)
        .eq(StringUtils.hasText(relationToHead), VillagePopulationEntity::getRelationToHead, relationToHead)
        .orderByDesc(VillagePopulationEntity::getCreateTime);
        IPage<VillagePopulationEntity> entityPage = page(new Page<>(current, size), queryWrapper);
        VillagePopulationListPageCache toSave = new VillagePopulationListPageCache();
        toSave.setRecords(entityPage.getRecords().stream().map(this::toPopulationSimpleVo).toList());
        toSave.setTotal(entityPage.getTotal());
        toSave.setCurrent(entityPage.getCurrent());
        toSave.setSize(entityPage.getSize());
        toSave.setPages(entityPage.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
        return entityPage.convert(this::toPopulationSimpleVo);
    }

    /**
     * 根据id获取人口台账详情
     * @param id 人口台账id
     * @return 人口台账详情
     */
    @Override
    public VillagePopulationDetailVO getVillagePopulationDetail(Long id) {
      String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
      if(redisJsonCacheTool.isNullMarker(cacheKey)){
          throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "人口台账不存在");
      }
      VillagePopulationDetailVO cached = redisJsonCacheTool.getObject(cacheKey, VillagePopulationDetailVO.class);
      if(cached!=null){
          return cached;
      }
        VillagePopulationEntity entity = getById(id);
        if (entity == null) {
          redisJsonCacheTool.setNullMarker(cacheKey);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "人口台账不存在");
        }
        VillagePopulationDetailVO vo = new VillagePopulationDetailVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id), vo);
        return vo;
    }

    /**
     * 更新人口台账
     * @param id 人口台账id
     * @param villagePopulationUpdateDTO 人口台账更新DTO
     */
   @Override
      public void updateVillagePopulation(Long id, VillagePopulationUpdateDTO villagePopulationUpdateDTO) {
          String lockKey = "lock:villagePopulation:update:" + id;
          String lockInstance = RedisDistributedLock.generateInstanceId();
          boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
          if (!locked) {
              throw new BusinessException(ErrorCode.SYSTEM_BUSY, "人口台账正在被修改，请稍后再试");
          }
          try {
              VillagePopulationEntity entity = getById(id);
              if (entity == null) {
                  throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "人口台账不存在");
              }
              BeanUtils.copyProperties(villagePopulationUpdateDTO, entity);
              updateById(entity);
              redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
              bumpListCacheVersion();
          } finally {
              redisDistributedLock.unlock(lockKey, lockInstance);
          }
      }


    /**
     * 删除人口台账
     * @param id 人口台账id
     */
    @Override
    public void deleteVillagePopulation(Long id) {
        String lockKey = "lock:villagePopulation:delete:" + id;
        String lockInstance = RedisDistributedLock.generateInstanceId();
        boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
        if (!locked) {
            throw new BusinessException(ErrorCode.SYSTEM_BUSY, "人口台账正在被操作，请稍后再试");
        }
        try {
            VillagePopulationEntity entity = getById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "人口台账不存在");
            }
            removeById(id);
            redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
            bumpListCacheVersion();
        } finally {
            redisDistributedLock.unlock(lockKey, lockInstance);
        }
    }
    private void bumpListCacheVersion(){
        redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
    }

    private VillagePopulationSimpleVO toPopulationSimpleVo(VillagePopulationEntity entity){
        VillagePopulationSimpleVO vo=new VillagePopulationSimpleVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}

