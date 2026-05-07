package com.backend.feature.service.impl;

import org.springframework.stereotype.Service;
import com.backend.feature.entity.FeatureEntity;
import com.backend.feature.mapper.FeatureMapper;
import com.backend.feature.service.FeatureService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.feature.dto.HighlightCreateDTO;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import com.backend.common.context.LoginUserContext;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.backend.feature.vo.FeatureVO;
import org.springframework.beans.BeanUtils;
import com.backend.common.exception.BusinessException;
import com.backend.common.enums.ErrorCode;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisJsonCacheTool;
import lombok.RequiredArgsConstructor;
import com.backend.feature.dto.FeaturePublishedPageCache;
import java.util.Map;
import java.util.Objects;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
/**
 * @author chenyang
 * &#064;date 2026/4/23
 * &#064;description 乡村风采服务实现类
 */
@Service
@RequiredArgsConstructor
public class FeatureServiceImpl extends ServiceImpl<FeatureMapper, FeatureEntity> implements FeatureService {

    private static final String CACHE_KEY_PREFIX = "feature:detail:";
    private static final String CACHE_LIST_KEY_PREFIX = "feature:list:";
    private static final String CACHE_LIST_VER_KEY = "feature:list:ver";
    private static final String CACHE_LIST_ADMIN_PREFIX = "feature:list:admin:";
    private static final String CACHE_LIST_ADMIN_VER_KEY = "feature:list:admin:ver";
    private final RedisJsonCacheTool redisJsonCacheTool;

    private final FeatureMapper featureMapper;

    /* 创建乡村风采 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createFeature(HighlightCreateDTO dto, HttpServletRequest request) {
        FeatureEntity entity = new FeatureEntity();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setType(dto.getType());
        entity.setCover(dto.getCover());
        if(dto.getVideo() != null) {
            entity.setVideo(dto.getVideo());
        }
        if(dto.getImages() != null) {
            entity.setImages(dto.getImages());
        }
        entity.setCreateUser(LoginUserContext.getAuthId(request));
        save(entity);
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, entity.getId()));
        bumpPublishedListCacheVersion();
        bumpAdminListCacheVersion();
    }

    /* 村民获取乡村风采列表 */
    @Override
    public IPage<FeatureVO> getFeatureList(Long current, Long size, String type, Integer getSort, LocalDateTime getCreateTime, LocalDateTime startTime, LocalDateTime endTime, HttpServletRequest request) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(CACHE_LIST_KEY_PREFIX, ver, current, size);
        FeaturePublishedPageCache cached = redisJsonCacheTool.getObject(listKey, FeaturePublishedPageCache.class);
        if (cached != null) {
            List<FeatureVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<FeatureVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            return hit;
        }
        LambdaQueryWrapper<FeatureEntity> wrapper = new LambdaQueryWrapper<FeatureEntity>()
        .eq(FeatureEntity::getStatus, 1)
        .eq(FeatureEntity::getDeleted, 0)
        .eq(type != null, FeatureEntity::getType, type)
        .ge(startTime != null, FeatureEntity::getCreateTime, startTime)
        .le(endTime != null, FeatureEntity::getCreateTime, endTime)
        .orderByDesc(getSort != null, FeatureEntity::getSort,FeatureEntity::getCreateTime);
        IPage<FeatureEntity> entityPage = page(new Page<>(current, size), wrapper);
        /* 转换为 VO */
        return entityPage.convert(this::toVo);
    }

    /* 获取乡村风采详情 */
    @Override
    public FeatureVO getFeatureDetail(Long id){
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        if (redisJsonCacheTool.isNullMarker(cacheKey)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
        }
        FeatureVO fromCache = tryLoadAndBumpFromCache(id, cacheKey);
        if (fromCache != null) {
            return fromCache;
        }
        
        FeatureEntity entity = getById(id);
        if (entity == null||entity.getStatus()!=1) {
            redisJsonCacheTool.isNullMarker(cacheKey);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
        }
        LambdaUpdateWrapper<FeatureEntity> wrapper=new LambdaUpdateWrapper<FeatureEntity>()
        .eq(FeatureEntity::getId,id)
        .setSql("view_count=IFNULL(view_count,0)+1");
        int rows = featureMapper.update(null, wrapper);
        if (rows <= 0) {
            redisJsonCacheTool.delete(cacheKey);
            return null;
        }
        int currentViewCount = entity.getViewCount() == null ? 0 : entity.getViewCount();
        entity.setViewCount(currentViewCount + 1);
        updateById(entity);

        FeatureVO vo = new FeatureVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        bumpPublishedListCacheVersion();
        return vo;
    }


    /* 尝试从缓存中加载并增加浏览量 */
    /**
     * 尝试从缓存中加载并增加浏览量
     * @param id 乡村风采 ID
     * @param cacheKey 缓存键
     * @return 乡村风采 VO
     */
    private FeatureVO tryLoadAndBumpFromCache(Long id, String cacheKey) {
        FeatureVO fromCache = redisJsonCacheTool.getObject(cacheKey, FeatureVO.class);
        if (fromCache == null) {
            return null;
        }
        FeatureEntity entity = getById(id);
        if (entity == null||!Objects.equals(entity.getStatus(), 1)) {
            redisJsonCacheTool.setNullMarker(cacheKey);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
        }
        LambdaUpdateWrapper<FeatureEntity> wrapper = new LambdaUpdateWrapper<FeatureEntity>()
        .eq(FeatureEntity::getId, id)
        .setSql("view_count = IFNULL(view_count, 0) + 1");
        int rows = featureMapper.update(null, wrapper);
        if (rows <= 0) {
            redisJsonCacheTool.delete(cacheKey);
            return null;
        }
        int current = fromCache.getViewCount() == null ? 0 : fromCache.getViewCount();
        fromCache.setViewCount(current + 1);
        redisJsonCacheTool.setObject(cacheKey, fromCache);
        bumpPublishedListCacheVersion();
        return fromCache;
    }


    /* 上下架乡村风采 */
    @Override
    public void updateStatus(Long id, Integer status, HttpServletRequest request) {
        FeatureEntity entity = getById(id);
        /* 如果实体不存在，则抛出异常 */
        if(entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
        }
        if(!Objects.equals(entity.getCreateUser(),LoginUserContext.getAuthId(request))){
            throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此乡村风采");
        }
        entity.setStatus(status);   
        updateById(entity);
        evictDetailCache(id);
        bumpPublishedListCacheVersion();
        bumpAdminListCacheVersion();
    }   

    /* 管理端获取乡村风采列表 */
    @Override
    public IPage<FeatureVO> getFeatureListByAdmin(Long current, Long size, Integer status, String title, String type, Integer getSort, LocalDateTime getCreateTime, LocalDateTime startTime, LocalDateTime endTime, HttpServletRequest request) {
        Integer uid = LoginUserContext.getAuthId(request);
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_ADMIN_VER_KEY);
        String prefix = CACHE_LIST_ADMIN_PREFIX + CacheKeyUtils.listFilterSegment(uid, status, title, type, getSort, getCreateTime, startTime, endTime);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        FeaturePublishedPageCache cached = redisJsonCacheTool.getObject(listKey, FeaturePublishedPageCache.class);
        if (cached != null) {
            List<FeatureVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<FeatureVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            return hit;
        }
        LambdaQueryWrapper<FeatureEntity> wrapper = new LambdaQueryWrapper<FeatureEntity>()
        .eq(FeatureEntity::getCreateUser, uid)
        .eq(FeatureEntity::getDeleted,0)
        .like(StringUtils.hasText(title), FeatureEntity::getTitle,title)
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
        return entityPage.convert(this::toVo);
    }

    /* 修改乡村风采 */
    @Override
    public void updateFeature(Long id, HighlightCreateDTO dto, HttpServletRequest request) {
        FeatureEntity entity = getById(id);
        /* 如果实体不存在，则抛出异常 */
        if(entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
        }
        /* 如果创建用户不匹配，则抛出异常 */
        if(!Objects.equals(entity.getCreateUser(),LoginUserContext.getAuthId(request))){
            throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此乡村风采");
        }
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setType(dto.getType());
        entity.setCover(dto.getCover());
        if(dto.getVideo() != null) {
            entity.setVideo(dto.getVideo());
        }
        if(dto.getImages() != null) {
            entity.setImages(dto.getImages());
        }
        updateById(entity);
        evictDetailCache(id);
        bumpPublishedListCacheVersion();
        bumpAdminListCacheVersion();
    }

    /* 删除乡村风采 */
    @Override
    public void deleteFeature(Long id, HttpServletRequest request) {
        FeatureEntity entity = getById(id);
        /* 如果实体不存在，则抛出异常 */
        if(entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
        }
        /* 如果创建用户不匹配，则抛出异常 */
        if(!Objects.equals(entity.getCreateUser(),LoginUserContext.getAuthId(request))){
            throw new BusinessException(ErrorCode.NO_PERMISSION, "您没有权限操作此乡村风采");
        }
        removeById(id);
        evictDetailCache(id);
        bumpPublishedListCacheVersion();
        bumpAdminListCacheVersion();
    }

    /* 类型统计 */
    @Override
    public Map<String, Long> getFeatureTypeStatistics() {
        String[] types = {"scenery", "product", "culture", "history"};
        Map<String, Long> result = new HashMap<>(types.length);

        for (String t : types) {
            long total = lambdaQuery()
                    .eq(FeatureEntity::getDeleted, 0)
                    .eq(FeatureEntity::getStatus, 1)
                    .eq(FeatureEntity::getType, t)
                    .count();
            result.put(t, total);
        }
        return result;
    }

    /* 获取我的乡村风采数量 */
    @Override
    public Map<String,Long> getMyFeatureCount(HttpServletRequest request){
        LambdaQueryWrapper<FeatureEntity> wrapper=new LambdaQueryWrapper<FeatureEntity>()
        .eq(FeatureEntity::getCreateUser,LoginUserContext.getAuthId(request));
        LambdaQueryWrapper<FeatureEntity> wrapper2=new LambdaQueryWrapper<FeatureEntity>()
        .eq(FeatureEntity::getCreateUser,LoginUserContext.getAuthId(request))
        .eq(FeatureEntity::getStatus,0);
        LambdaQueryWrapper<FeatureEntity> wrapper3=new LambdaQueryWrapper<FeatureEntity>()
        .eq(FeatureEntity::getCreateUser,LoginUserContext.getAuthId(request))
        .eq(FeatureEntity::getStatus,1);
        Map<String,Long> result = new HashMap<>();
        result.put("total",count(wrapper));
        result.put("In reality",count(wrapper3));
        result.put("Hidden",count(wrapper2));
        return result;
    }
    
    /* 使所有「已发布列表」分页缓存失效：版本号 +1，读侧使用新前缀 */
    public void bumpPublishedListCacheVersion() {
        redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
    }

    private void bumpAdminListCacheVersion() {
        redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_ADMIN_VER_KEY);
    }
    
    /* 写操作后删除对应详情缓存，避免脏读 */
    private void evictDetailCache(Long id) {
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
    }

    /* 转换为 VO */
    private FeatureVO toVo(FeatureEntity entity) {
        FeatureVO vo = new FeatureVO();
        BeanUtils.copyProperties(Objects.requireNonNull(entity), vo);
        return vo;
    }
}