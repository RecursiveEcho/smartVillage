package com.backend.announcement.service.impl;

import com.backend.announcement.dto.AnnouncementCreateDTO;
import com.backend.announcement.dto.AnnouncementUpdateDTO;
import com.backend.announcement.entity.AnnouncementEntity;
import com.backend.announcement.mapper.AnnouncementMapper;
import com.backend.announcement.service.AnnouncementService;
import com.backend.announcement.vo.AnnouncementVO;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisJsonCacheTool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 公告业务：CRUD、前台分页/热门、详情浏览量与 Redis 详情缓存。
 * <p>
 * 状态约定：{@link #STATUS_PUBLISHED} 表示前台可见；{@code updateStatus} 仅接受 0/1。
 * 详情接口先读缓存；缓存命中时用 SQL 对 {@code view_count} 做原子 +1 并同步 VO，未命中则查库更新后回写缓存。
 */
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, AnnouncementEntity>
        implements AnnouncementService {

    /** Redis 中单条详情 JSON 的 key 前缀 */
    private static final String CACHE_KEY_PREFIX = "announcement:detail:";


    /** 与前台列表、热门查询一致：仅 status=1 视为已发布 */
    private static final int STATUS_PUBLISHED = 1;
    private static final int DELETED_YES = 1;
    private static final int DEFAULT_HOT_LIMIT = 5;

    private final AnnouncementMapper announcementMapper;
    private final RedisJsonCacheTool redisJsonCacheTool;

    /** 新建即发布：写发布时间、浏览量 0、未删除 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(AnnouncementCreateDTO dto) {
        AnnouncementEntity entity = new AnnouncementEntity();
        /* 设置标题、内容、状态、类型、是否置顶、发布时间、浏览量、是否删除 */
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setStatus(STATUS_PUBLISHED);
        entity.setType(dto.getType());
        entity.setIsTop(dto.getIsTop());
        entity.setPublishTime(LocalDateTime.now());
        entity.setViewCount(0);
        entity.setDeleted(0);
        /* 保存实体 */
        save(entity);
    }

    /// 前台列表：已发布 + 未删除，置顶优先再按发布时间倒序
    @Override
    public IPage<AnnouncementVO> pagePublished(Long current, Long size) {
        /* 查询条件：状态为已发布，未删除，置顶优先，再按发布时间倒序 */
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<AnnouncementEntity>()
                .eq(AnnouncementEntity::getStatus, STATUS_PUBLISHED)
                .eq(AnnouncementEntity::getDeleted, 0)
                .orderByDesc(AnnouncementEntity::getIsTop)
                .orderByDesc(AnnouncementEntity::getPublishTime);
        Page<AnnouncementEntity> page = announcementMapper.selectPage(new Page<>(current, size), wrapper);
        /* 转换为 VO 并返回 */
        return page.convert(entity->{
            AnnouncementVO vo = new AnnouncementVO();
            BeanUtils.copyProperties(Objects.requireNonNull(entity), vo);
            return vo;
        });
    }

    /* 校验标题/内容/类型后整行更新，并清除详情缓存 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAnnouncement(Long id, AnnouncementUpdateDTO dto) {
        /* 构建缓存 key */
        AnnouncementEntity entity = getActiveOrThrow(id);
        /* 设置标题、内容、类型、是否置顶 */
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setType(dto.getType());
        entity.setIsTop(dto.getIsTop());
        /* 更新实体并清除详情缓存 */
        updateById(entity);
        evictDetailCache(id);
    }

    /**
     * 上下架：{@code status} 必须为 0 或 1。若为发布且尚无发布时间，则写入当前时间并记录审核时间。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        /* 获取实体并校验 */
        AnnouncementEntity entity = getActiveOrThrow(id);
        /* 设置状态 */
        entity.setStatus(status);
        /* 如果状态为已发布，则设置发布时间、审核时间 */
        if (Objects.equals(status, STATUS_PUBLISHED)) {
            if (entity.getPublishTime() == null) {
                entity.setPublishTime(LocalDateTime.now());
            }
            entity.setAuditTime(LocalDateTime.now());
        }
        /* 更新实体并清除详情缓存 */
        updateById(entity);
        evictDetailCache(id);
    }

    /**
     * 读详情：优先缓存；缓存未命中则查库、浏览量 +1、写缓存。缓存命中路径见 {@link #tryLoadAndBumpFromCache}。
     */
    @Override
    public AnnouncementVO getAnnouncement(Long id) {
        /* 构建缓存 key */
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        /* 尝试从缓存中获取 */
        AnnouncementVO fromCache = tryLoadAndBumpFromCache(id, cacheKey);
        /* 如果缓存命中，则返回 */
        if (fromCache != null) {
            return fromCache;
        }

        /* 获取实体并累加浏览量 */
        AnnouncementEntity entity = getActiveOrThrow(id);
        int nextViews = (entity.getViewCount() == null ? 0 : entity.getViewCount()) + 1;
        entity.setViewCount(nextViews);
        updateById(entity);
        /* 转换为 VO 并写入缓存 */
        AnnouncementVO vo = toVo(entity);
        writeDetailCache(cacheKey, vo);
        return vo;
    }

    /** 已发布列表按浏览量降序，浏览量相同时按创建时间升序，限制条数 */
    @Override
    public List<AnnouncementVO> listHot(Integer limit) {
        /* 设置默认条数 */
        int n = (limit == null || limit <= 0) ? DEFAULT_HOT_LIMIT : limit;
        /* 查询条件：状态为已发布，未删除，按浏览量降序，按创建时间升序，限制条数 */
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<AnnouncementEntity>()
                .eq(AnnouncementEntity::getStatus, STATUS_PUBLISHED)
                .eq(AnnouncementEntity::getDeleted, 0)
                .orderByDesc(AnnouncementEntity::getViewCount)
                .orderByAsc(AnnouncementEntity::getCreateTime)
                .last("limit " + n);
        /* 转换为 VO 并返回 */
        return announcementMapper.selectList(wrapper).stream().map(this::toVo).toList();
    }

    /* 逻辑删除并清理缓存 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnouncement(Long id) {
        /* 获取实体并校验 */
        AnnouncementEntity entity = getActiveOrThrow(id);
        /* 设置删除标志 */
        entity.setDeleted(DELETED_YES);
        /* 更新实体并清除详情缓存 */
        updateById(entity);
        evictDetailCache(id);
    }


    /* 管理员分页查询公告 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<AnnouncementVO> pageAdmin(Long current, Long size,Integer status) {
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<AnnouncementEntity>()
                .eq(AnnouncementEntity::getDeleted, 0)
                .eq(status != null, AnnouncementEntity::getStatus, status)
                .orderByDesc(AnnouncementEntity::getUpdateTime);
        Page<AnnouncementEntity> page = announcementMapper.selectPage(new Page<>(current, size), wrapper);
        return page.convert(this::toVo);
    }
    /**
     * 缓存命中：反序列化 VO，用 {@code UPDATE ... SET view_count = IFNULL(view_count,0)+1} 原子加浏览量；
     * 更新失败则删缓存回落到 DB 路径；JSON 损坏亦删缓存。
     */
    private AnnouncementVO tryLoadAndBumpFromCache(Long id, String cacheKey) {
        AnnouncementVO vo = redisJsonCacheTool.getObject(cacheKey, AnnouncementVO.class);
        if (vo == null) {
            return null;
        }
        LambdaUpdateWrapper<AnnouncementEntity> bump = new LambdaUpdateWrapper<AnnouncementEntity>()
                .eq(AnnouncementEntity::getId, id.intValue())
                .eq(AnnouncementEntity::getDeleted, 0)
                .setSql("view_count = IFNULL(view_count,0) + 1");
        int rows = announcementMapper.update(null, bump);
        if (rows > 0) {
            int vc = vo.getViewCount() == null ? 0 : vo.getViewCount();
            vo.setViewCount(vc + 1);
            return vo;
        }
        redisJsonCacheTool.delete(cacheKey);
        return null;
    }

    /** 序列化 VO 写入 Redis，TTL 见 {@link #CACHE_TTL} */
    private void writeDetailCache(String cacheKey, AnnouncementVO vo) {
        redisJsonCacheTool.setObject(cacheKey, vo);
    }

    /** 获取实体并校验 */
    private AnnouncementEntity getActiveOrThrow(Long id) {
        /* 获取实体并校验 */
        AnnouncementEntity entity = getById(id);
        /* 如果实体不存在或已删除，则抛出业务异常 */
        if (entity == null || Objects.equals(entity.getDeleted(), DELETED_YES)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        /* 返回实体 */
        return entity;
    }

    private AnnouncementVO toVo(AnnouncementEntity entity) {
        AnnouncementVO vo = new AnnouncementVO();
        BeanUtils.copyProperties(Objects.requireNonNull(entity), vo);
        return vo;
    }

    /** 写操作后删除对应详情缓存，避免脏读 */
    private void evictDetailCache(Long id) {
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
    }
}
