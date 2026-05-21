package com.backend.interaction.service.impl;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.common.context.LoginUserContext;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
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
 * @author chenyang
 * &#064;date  2026/4/15
 * &#064;description  村民留言业务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class InteractionServiceImpl extends ServiceImpl<InteractionMapper, InteractionEntity> implements InteractionService {

    private final RedisRateLimiter redisRateLimiter;
    private final RedisJsonCacheTool redisJsonCacheTool;
    private final InteractionMapper interactionMapper;
    private final AuthMapper authMapper;
    private static final String CACHE_KEY_PREFIX = "interaction:detail:";
    private static final String CACHE_LIST_VER_KEY = "interaction:list:ver";
    /** 村民端公开列表（无筛选） */
    private static final String CACHE_LIST_PUB_PREFIX = "interaction:list:pub:";
    /** 管理端带条件列表 */
    private static final String CACHE_LIST_CADRE_PREFIX = "interaction:list:cadre:";
    /** 我的留言（按用户隔离） */
    private static final String CACHE_LIST_MINE_PREFIX = "interaction:list:mine:";

    /* 新增村民留言 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InteractionCreateVO createMessage(InteractionCreateDTO dto, HttpServletRequest request) {

      // 限流：每人每分钟最多 5 次创建（根据用户 ID 和 IP 共同限流，防止账号共享导致的滥用）
      String clientIp=request.getRemoteAddr();
      Integer userId =LoginUserContext.getAuthId(request);
      String rateKey = "rate_limit:interaction:create:" + userId + ":" + clientIp;
      redisRateLimiter.check(rateKey,5);

        InteractionEntity entity = new InteractionEntity();
        entity.setUserId(LoginUserContext.getAuthId(request));
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        InteractionCreateVO vo = new InteractionCreateVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setUsername(LoginUserContext.getUsername(request));
        redisJsonCacheTool.setObject(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, entity.getId()), vo);
        BumpListCacheVersion();
        return  vo;
    }

    /* 获取村民留言列表 */
    @Override
    public IPage<InteractionDetailVO> getMessageList(Long current, Long size) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_PUB_PREFIX + CacheKeyUtils.listFilterSegment();
        String listCacheKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);

        InteractionPublishedPageCache cached = redisJsonCacheTool.getObject(listCacheKey, InteractionPublishedPageCache.class);

        if (cached != null) {
            List<InteractionDetailVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();

            enrichUsernames(rows);
            Page<InteractionDetailVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            return hit;
        }

        LambdaQueryWrapper<InteractionEntity> wrapper = new LambdaQueryWrapper<InteractionEntity>()
        .orderByDesc(InteractionEntity::getCreateTime);
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
        Page<InteractionDetailVO> out = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        out.setRecords(voRows);
        return out;
    }

    /* 回复村民留言 */
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

    /* 获取村民留言详情 */
    @Override
    public InteractionDetailVO getMessageDetail(Long id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        if (redisJsonCacheTool.isNullMarker(cacheKey)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "留言不存在");
        }
        InteractionDetailVO fromCache = tryLoadAndBumpFromCache(id, cacheKey);
        if (fromCache != null) {
            return fromCache;
        }
        InteractionEntity entity = interactionMapper.selectById(id);
        if (entity == null) {
            redisJsonCacheTool.setNullMarker(cacheKey);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "留言不存在");
        }
        InteractionDetailVO vo = new InteractionDetailVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        return vo;
    }



    /* 管理端获取村民留言列表 */
    @Override
    public IPage<InteractionDetailVO> getMessageListByCadre(Long current, Long size, Integer status, String type, LocalDateTime startTime, LocalDateTime endTime) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_CADRE_PREFIX + CacheKeyUtils.listFilterSegment(status, type, startTime, endTime);
        String listCacheKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        InteractionPublishedPageCache cached = redisJsonCacheTool.getObject(listCacheKey, InteractionPublishedPageCache.class);
        if (cached != null) {
            List<InteractionDetailVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<InteractionDetailVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            return hit;
        }
        LambdaQueryWrapper<InteractionEntity> wrapper = new LambdaQueryWrapper<InteractionEntity>()
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

    /* 我的留言 */
    @Override
    public IPage<InteractionDetailVO> getMyMessageList(HttpServletRequest request, Long current, Long size) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        Integer userId = LoginUserContext.getAuthId(request);
        String prefix = CACHE_LIST_MINE_PREFIX + CacheKeyUtils.listFilterSegment(userId);
        String listCacheKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        InteractionPublishedPageCache cached = redisJsonCacheTool.getObject(listCacheKey, InteractionPublishedPageCache.class);
        if (cached != null) {
            List<InteractionDetailVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<InteractionDetailVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            return hit;
        }
        LambdaQueryWrapper<InteractionEntity> wrapper = new LambdaQueryWrapper<InteractionEntity>()
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

    /*我的留言详细 */
    @Override
    public InteractionDetailVO getMyMessageDetail(HttpServletRequest request, Long id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        if (redisJsonCacheTool.isNullMarker(cacheKey)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "留言不存在");
        }
        InteractionDetailVO fromCache = tryLoadAndBumpFromCache(id, cacheKey, request);
        if (fromCache != null) {
            return fromCache;
        }
        InteractionEntity entity = interactionMapper.selectById(id);
        if (entity == null||!Objects.equals(entity.getUserId(), LoginUserContext.getAuthId(request))||entity.getStatus() != 0) {
            redisJsonCacheTool.setNullMarker(cacheKey);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "留言不存在");
        }
        InteractionDetailVO vo = new InteractionDetailVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        return vo;
    }

    /* 尝试从缓存中加载并更新版本 */
    private InteractionDetailVO tryLoadAndBumpFromCache(Long id, String cacheKey, HttpServletRequest request) {
        InteractionDetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, InteractionDetailVO.class);
        if (fromCache == null) {
            return null;
        }
        InteractionEntity entity = interactionMapper.selectById(id);
        if (entity == null||!Objects.equals(entity.getUserId(), LoginUserContext.getAuthId(request))||entity.getStatus() != 0) {
            redisJsonCacheTool.delete(cacheKey);
            return null;
        }
        return fromCache;
    }

    /*村民撤回留言 */
    @Override
    public String withdrawMessage(HttpServletRequest request, Long id) {
        LambdaQueryWrapper<InteractionEntity> wrapper = new LambdaQueryWrapper<InteractionEntity>()
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

    /*管理端处理村民留言 */
    @Override
    public String processingMessage(Long id, HttpServletRequest request) {
        InteractionEntity entity = requireById(id);
        if (entity.getStatus() != null && entity.getStatus() == 3) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "留言已关闭，无法处理");
        }
        entity.setStatus(1);
        updateById(entity);
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
        BumpListCacheVersion();
        return "处理成功";
    }

    /* 获取实体 */
    private InteractionEntity requireById(Long id) {
        InteractionEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "留言不存在");
        }
        return entity;
    }

    /* 转换为创建页VO */
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
        for(InteractionDetailVO vo:rows){
            if(vo.getUserId()!=null) ids.add(vo.getUserId());
            if(vo.getReplyUser()!=null) ids.add(vo.getReplyUser());
        }
        if (ids.isEmpty()) {
            return;
        }
        List<AuthEntity> auths = authMapper.selectList(
                new LambdaQueryWrapper<AuthEntity>().in(AuthEntity::getId, ids));
        Map<Integer, String> idToName = new HashMap<>();
        for (AuthEntity a : auths) {
            if (a.getId() != null) {
                idToName.put(a.getId(), a.getUsername());
            }
        }
        for (InteractionDetailVO vo : rows) {
            if (vo.getUserId()!= null) {
                vo.setUsername(idToName.get(vo.getUserId()));
            }
            if(vo.getReplyUsername()!= null){
                vo.setReplyUsername(idToName.get(vo.getReplyUser()));
            }
        }
    }

    /* 转换为详情页VO */
    public InteractionDetailVO toDetailVo(InteractionEntity entity) {
        InteractionDetailVO vo = new InteractionDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /* 更新列表缓存版本 */
    public void BumpListCacheVersion() {
        redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
    }

    /* 尝试从缓存中加载并更新版本 */
    private InteractionDetailVO tryLoadAndBumpFromCache(Long id, String cacheKey) {
        InteractionDetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, InteractionDetailVO.class);
        if (fromCache == null) {
            return null;
        }
        InteractionEntity entity = interactionMapper.selectById(id);
        if (entity == null) {
            redisJsonCacheTool.delete(cacheKey);
            return null;
        }
        return fromCache;
    }
}
