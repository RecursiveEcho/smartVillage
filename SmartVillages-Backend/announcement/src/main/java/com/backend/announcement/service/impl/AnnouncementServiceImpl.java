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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.util.StringUtils;
import com.backend.common.context.LoginUserContext;
/**
 * 公告业务：CRUD、前台分页/热门、详情浏览量与 Redis 详情缓存。
 * <p>
 * 状态约定：0-待审核 1-已通过(发布) 2-已拒绝 3-已下架。
 * 详情接口先读缓存；缓存命中时用 SQL 对 {@code view_count} 做原子 +1 并同步 VO，未命中则查库更新后回写缓存。
 */
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, AnnouncementEntity>
        implements AnnouncementService {

    /** Redis 中单条详情 JSON 的 key 前缀 */
    private static final String CACHE_KEY_PREFIX = "announcement:detail:";


    private static final int STATUS_PENDING = 0;
    /** 与前台列表、热门查询一致：仅 status=1 视为已发布 */
    private static final int STATUS_PUBLISHED = 1;
    private static final int STATUS_OFFLINE = 3;
    private static final int DELETED_YES = 1;
    private static final int DEFAULT_HOT_LIMIT = 5;

    private final AnnouncementMapper announcementMapper;
    private final RedisJsonCacheTool redisJsonCacheTool;

    /** 新建默认待审核：浏览量 0、未删除 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAnnouncement(AnnouncementCreateDTO dto, HttpServletRequest request) {
        AnnouncementEntity entity = new AnnouncementEntity();
        /* 设置标题、内容、状态、类型、是否置顶、发布时间、浏览量、是否删除 */
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setStatus(STATUS_PENDING);
        entity.setType(dto.getType());
        entity.setIsTop(dto.getIsTop());
        entity.setPublishTime(LocalDateTime.now());
        entity.setViewCount(0);
        entity.setDeleted(0);
        entity.setCreateUser(LoginUserContext.getAuthId(request));
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

    /** 上下架状态更新 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status, HttpServletRequest request) {
        /* 获取实体并校验 */
        AnnouncementEntity entity = getActiveOrThrow(id);
        /* 设置状态 */
        entity.setStatus(status);
        entity.setUpdateTime(LocalDateTime.now());
        entity.setAuditUser(LoginUserContext.getAuthId(request));
        /* 如果状态为已发布，则设置发布时间、审核时间 */
        if (Objects.equals(status, STATUS_PUBLISHED)) {
            entity.setPublishTime(LocalDateTime.now());
            entity.setAuditTime(LocalDateTime.now());
        }


        /* 如果状态为已下架，则设置下架时间、下架人 */
        if (Objects.equals(status, STATUS_OFFLINE)) {
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

    /** 管理员公告详情*/
    @Override
    public AnnouncementVO getAdminAnnouncement(Long id) {
        return toVo(getActiveOrThrow(id));
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
    public IPage<AnnouncementVO> pageAdmin(Long current, Long size,Integer status, String title,Integer type,Integer isTop,LocalDateTime startTime,LocalDateTime endTime) {
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<AnnouncementEntity>()
                .eq(AnnouncementEntity::getDeleted, 0)
                .eq(status != null, AnnouncementEntity::getStatus, status)
                .like(StringUtils.hasText(title), AnnouncementEntity::getTitle, title)
                .eq(type != null, AnnouncementEntity::getType, type)
                .eq(isTop != null, AnnouncementEntity::getIsTop, isTop)
                .ge(startTime != null, AnnouncementEntity::getPublishTime, startTime)
                .le(endTime != null, AnnouncementEntity::getPublishTime, endTime)
                .orderByDesc(AnnouncementEntity::getUpdateTime);
        Page<AnnouncementEntity> page = announcementMapper.selectPage(new Page<>(current, size), wrapper);
        return page.convert(this::toVo);
    }

    /* 管理员待审核公告 */
    @Override
    public IPage<AnnouncementVO> pagePending(Long current, Long size, String title,Integer type,Integer isTop,LocalDateTime startTime,LocalDateTime endTime) {
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<AnnouncementEntity>()
                .eq(AnnouncementEntity::getDeleted, 0)
                .eq(AnnouncementEntity::getStatus, STATUS_PENDING)
                .like(StringUtils.hasText(title), AnnouncementEntity::getTitle, title)
                .eq(type != null, AnnouncementEntity::getType, type)
                .eq(isTop != null, AnnouncementEntity::getIsTop, isTop)
                .ge(startTime != null, AnnouncementEntity::getPublishTime, startTime)
                .le(endTime != null, AnnouncementEntity::getPublishTime, endTime)
                .orderByDesc(AnnouncementEntity::getUpdateTime);
        Page<AnnouncementEntity> page = announcementMapper.selectPage(new Page<>(current, size), wrapper);
        return page.convert(this::toVo);
    }

    /* 管理员审核公告 */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void auditAnnouncement(Long id, Integer status, HttpServletRequest request) {
   AnnouncementEntity entity = getActiveOrThrow(id);
   entity.setAuditTime(LocalDateTime.now());
   entity.setAuditUser(LoginUserContext.getAuthId(request));
   if(status==1){
    entity.setPublishTime(LocalDateTime.now());
   }
   entity.setStatus(status);
   updateById(entity);
   evictDetailCache(id);
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

    /** 序列化 VO 写入 Redis */
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
