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
import com.backend.feature.vo.FeatureVO;
import org.springframework.beans.BeanUtils;
import com.backend.common.exception.BusinessException;
import com.backend.common.enums.ErrorCode;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisJsonCacheTool;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Objects;

import java.time.LocalDateTime;
import java.util.HashMap;
/**
 * @author chenyang
 * &#064;date 2026/4/23
 * &#064;description 乡村风采服务实现类
 */
@Service
@RequiredArgsConstructor
public class FeatureServiceImpl extends ServiceImpl<FeatureMapper, FeatureEntity> implements FeatureService {

    private static final String CACHE_KEY_PREFIX = "feature:detail:";

    private final RedisJsonCacheTool redisJsonCacheTool;

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
    }

    /* 村民获取乡村风采列表 */
    @Override
    public IPage<FeatureVO> getFeatureList(Long current, Long size, String type, Integer getSort, LocalDateTime getCreateTime, LocalDateTime startTime, LocalDateTime endTime, HttpServletRequest request) {
        LambdaQueryWrapper<FeatureEntity> wrapper = new LambdaQueryWrapper<FeatureEntity>()
        .eq(FeatureEntity::getStatus, 1)
        .eq(FeatureEntity::getDeleted, 0)
        .eq(type != null, FeatureEntity::getType, type)
        .ge(startTime != null, FeatureEntity::getCreateTime, startTime)
        .le(endTime != null, FeatureEntity::getCreateTime, endTime)
        .orderByDesc(getSort != null, FeatureEntity::getSort,FeatureEntity::getCreateTime);
        IPage<FeatureEntity> entityPage = page(new Page<>(current, size), wrapper);
        /* 转换为 VO */
        return entityPage.convert(entity -> {
            FeatureVO vo = new FeatureVO();
            BeanUtils.copyProperties(Objects.requireNonNull(entity), vo);
            return vo;
        });
    }

    /* 获取乡村风采详情 */
    @Override
    public FeatureVO getFeatureDetail(Long id){
        /* 构建缓存 key */
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        FeatureVO fromCache =redisJsonCacheTool.getObject(cacheKey, FeatureVO.class);
        /* 获取实体 */
        FeatureEntity entity = getById(id);
        /* 如果实体不存在，则抛出异常 */
        if(entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "乡村风采不存在");
        }
        /* 如果缓存命中，则累加浏览量 */
        if(fromCache != null) {
            int nextViewCount = entity.getViewCount() + 1;
            entity.setSort(nextViewCount);
            updateById(entity);
            return fromCache;
        }
        /* 转换为 VO */
        FeatureVO vo = new FeatureVO();
        /* 累加浏览量 */
        int nextSort = entity.getSort() + 1;
        entity.setSort(nextSort);
        updateById(entity);
        /* 转换为 VO */
        BeanUtils.copyProperties(entity, vo);
        /* 写入缓存 */
        redisJsonCacheTool.setObject(cacheKey, vo);
        return vo;   
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
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
    }   

    /* 管理端获取乡村风采列表 */
    @Override
    public IPage<FeatureVO> getFeatureListByAdmin(Long current, Long size, Integer status, String title, String type, Integer getSort, LocalDateTime getCreateTime, LocalDateTime startTime, LocalDateTime endTime, HttpServletRequest request) {
        LambdaQueryWrapper<FeatureEntity> wrapper = new LambdaQueryWrapper<FeatureEntity>()
        .eq(FeatureEntity::getCreateUser, LoginUserContext.getAuthId(request))
        .eq(FeatureEntity::getDeleted,0)
        .like(StringUtils.hasText(title), FeatureEntity::getTitle,title)
        .eq(type != null, FeatureEntity::getType, type)
        .ge(startTime != null, FeatureEntity::getCreateTime, startTime)
        .le(endTime != null, FeatureEntity::getCreateTime, endTime)
        .eq(status != null, FeatureEntity::getStatus, status)
        .orderByDesc(getSort != null, FeatureEntity::getSort)
        .orderByDesc(getCreateTime != null, FeatureEntity::getCreateTime); 
        IPage<FeatureEntity> entityPage = page(new Page<>(current, size), wrapper);
        return entityPage.convert(entity -> {
            FeatureVO vo = new FeatureVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
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
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
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
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
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

    @Override
    public Map<String,Long> getMyFeatureCount(HttpServletRequest request){
        LambdaQueryWrapper<FeatureEntity> wrapper=new LambdaQueryWrapper<FeatureEntity>()
        .eq(FeatureEntity::getCreateUser,LoginUserContext.getAuthId(request));
        Map<String,Long> result = new HashMap<>();
        result.put("total",count(wrapper));
        result.put("In reality",count(wrapper.eq(FeatureEntity::getStatus,1)));
        result.put("Hidden",count(wrapper.eq(FeatureEntity::getStatus,0)));
        return result;
    }

}