package com.backend.management.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisDistributedLock;
import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.management.dto.VillagePartyCreateDTO;
import com.backend.management.dto.VillagePartyListPageCache;
import com.backend.management.dto.VillagePartyUpdateDTO;
import com.backend.management.entity.VillagePartyEntity;
import com.backend.management.mapper.VillagePartyMapper;
import com.backend.management.service.VillagePartyService;
import com.backend.management.vo.VillagePartyDetailVO;
import com.backend.management.vo.VillagePartySimpleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VillagePartyServiceImpl
        extends ServiceImpl<VillagePartyMapper, VillagePartyEntity>
        implements VillagePartyService {

    private static final String CACHE_KEY_PREFIX = "village_party:";
    private static final String CACHE_LIST_VER_KEY = "village-party:list:ver";
    private static final String CACHE_LIST_PREFIX = "village-party:list:";
    private final RedisJsonCacheTool redisJsonCacheTool;
    private final RedisDistributedLock redisDistributedLock;
    /**
     * 创建党组织
     * @param dto 党组织创建DTO
     * @return 党组织ID
     */
    @Override
    public  Integer create(VillagePartyCreateDTO dto){
        VillagePartyEntity entity=new VillagePartyEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        Integer id=entity.getId();
          redisJsonCacheTool.setObject(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id), entity);
        bumpListCacheVersion();
        return id;
    }

    /**
     * 分页查询党建组织信息列表
     * @param current 当前页
     * @param size 每页条数
     * @param orgName 党组织名称
     * @param orgType 组织类型
     * @param secretaryName 书记姓名
     * @return 党建组织信息列表
     */
    @Override
    public IPage<VillagePartySimpleVO> getList(Long current, Long size, String orgName, String orgType, String secretaryName) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_PREFIX + CacheKeyUtils.listFilterSegment(orgName, orgType, secretaryName);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        VillagePartyListPageCache cached = redisJsonCacheTool.getObject(listKey, VillagePartyListPageCache.class);
        if (cached != null) {
            List<VillagePartySimpleVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<VillagePartySimpleVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            return hit;
        }
        LambdaQueryWrapper<VillagePartyEntity> queryWrapper = new LambdaQueryWrapper<VillagePartyEntity>()
        .like(StringUtils.hasText(orgName), VillagePartyEntity::getOrgName, orgName)
        .like(StringUtils.hasText(orgType), VillagePartyEntity::getOrgType, orgType)
        .like(StringUtils.hasText(secretaryName), VillagePartyEntity::getSecretaryName, secretaryName)
        .orderByDesc(VillagePartyEntity::getCreateTime);
        IPage<VillagePartyEntity> entityPage = page(new Page<>(current, size), queryWrapper);
        VillagePartyListPageCache toSave = new VillagePartyListPageCache();
        toSave.setRecords(entityPage.getRecords().stream().map(this::toPartySimpleVo).toList());
        toSave.setTotal(entityPage.getTotal());
        toSave.setCurrent(entityPage.getCurrent());
        toSave.setSize(entityPage.getSize());
        toSave.setPages(entityPage.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
        return entityPage.convert(this::toPartySimpleVo);
    }

    /**
     * 根据id获取党建组织信息详情
     * @param id 党建组织信息id
     * @return 党建组织信息详情
     */
    @Override
    public VillagePartyDetailVO getDetail(Integer id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        if(redisJsonCacheTool.isNullMarker(cacheKey)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "党建组织信息不存在");
         }
        VillagePartyDetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, VillagePartyDetailVO.class);
        if (fromCache != null) {
            return fromCache;
        }
        VillagePartyEntity entity = getById(id);
        if (entity == null) {
            redisJsonCacheTool.setNullMarker(cacheKey);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "党建组织信息不存在");
        }
        VillagePartyDetailVO vo = new VillagePartyDetailVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        return vo;
    }

    /**
     * 更新党建组织信息
     * @param id 党建组织信息id
     * @param dto 党建组织信息更新DTO
     */
    @Override
      public void update(Integer id, VillagePartyUpdateDTO dto) {
          String lockKey = "lock:villageParty:update:" + id;
          String lockInstance = RedisDistributedLock.generateInstanceId();
          boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
          if (!locked) {
              throw new BusinessException(ErrorCode.SYSTEM_BUSY, "党建组织正在被修改，请稍后再试");
          }
          try {
              VillagePartyEntity entity = getById(id);
              if (entity == null) {
                  throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "党建组织信息不存在");
              }
              BeanUtils.copyProperties(dto, entity);
              updateById(entity);
              redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
              bumpListCacheVersion();
          } finally {
              redisDistributedLock.unlock(lockKey, lockInstance);
          }
      }

    /**
     * 删除党建组织信息
     * @param id 党建组织信息id
     */
    @Override
        public void delete(Integer id) {
            String lockKey = "lock:villageParty:delete:" + id;
            String lockInstance = RedisDistributedLock.generateInstanceId();
            boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
            if (!locked) {
                throw new BusinessException(ErrorCode.SYSTEM_BUSY, "党建组织正在被操作，请稍后再试");
            }
            try {
                VillagePartyEntity entity = getById(id);
                if (entity == null) {
                    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "党建组织信息不存在");
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

        private VillagePartySimpleVO toPartySimpleVo(VillagePartyEntity entity){
          VillagePartySimpleVO vo=new VillagePartySimpleVO();
          BeanUtils.copyProperties(entity, vo);
          return vo;
        }
}

