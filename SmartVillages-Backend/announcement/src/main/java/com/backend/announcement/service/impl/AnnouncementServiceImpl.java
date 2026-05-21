package com.backend.announcement.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.backend.announcement.dto.AnnouncementCreateDTO;
import com.backend.announcement.dto.AnnouncementUpdateDTO;
import com.backend.announcement.entity.AnnouncementEntity;
import com.backend.announcement.mapper.AnnouncementMapper;
import com.backend.announcement.service.AnnouncementService;
import com.backend.announcement.vo.AnnouncementPublishedPageCache;
import com.backend.announcement.vo.AnnouncementVO;
import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.common.context.LoginUserContext;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisJsonCacheTool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

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
    /** 列表缓存版本号：写操作 INCR 后，各列表分页 key 自然失效 */
    private static final String CACHE_LIST_VER_KEY = "announcement:list:published:ver";
    private static final String CACHE_LIST_PUB_PREFIX = "announcement:list:published:";
    private static final String CACHE_LIST_ADMIN_PREFIX = "announcement:list:admin:";
    private static final String CACHE_LIST_PENDING_PREFIX = "announcement:list:pending:";
    private static final String CACHE_LIST_AUDITED_PREFIX = "announcement:list:audited:";

    private static final int STATUS_PENDING = 0;
    /** 与前台列表、热门查询一致：仅 status=1 视为已发布 */
    private static final int STATUS_PUBLISHED = 1;
    private static final int STATUS_REJECTED = 2;
    private static final int STATUS_OFFLINE = 3;
    private static final int DEFAULT_HOT_LIMIT = 5;


    private final AnnouncementMapper announcementMapper;
    private final RedisJsonCacheTool redisJsonCacheTool;
    private final ObjectMapper objectMapper;
     private final AuthMapper authMapper;

    /** 新建默认待审核：浏览量 0、未删除 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAnnouncement(AnnouncementCreateDTO dto, HttpServletRequest request) {
        AnnouncementEntity entity = new AnnouncementEntity();
        /* 设置标题、内容、状态、类型、是否置顶、浏览量（发布时间在“通过/上架”时写入） */
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setStatus(STATUS_PENDING);
        entity.setType(dto.getType());
        entity.setIsTop(dto.getIsTop());
        entity.setViewCount(0);
        entity.setCreateUser(LoginUserContext.getAuthId(request));
        entity.setCreateTime(null);
        /* 保存实体 */
        save(entity);
        bumpPublishedListCacheVersion();
    }

    /// 前台列表：已发布 + 未删除，置顶优先再按发布时间倒序
    @Override
    public IPage<AnnouncementVO> pagePublished(Long current, Long size) {
        /* 获取版本号 */
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_PUB_PREFIX + CacheKeyUtils.listFilterSegment();
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);

        AnnouncementPublishedPageCache cached = redisJsonCacheTool.getObject(listKey, AnnouncementPublishedPageCache.class);
        // 列表「缓存穿透」：空页也写入 records=[]，命中后不再打库；records 为 null 时按空列表返回，避免坏 JSON 反复回源
        if (cached != null) {
            List<AnnouncementVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<AnnouncementVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            enrichUsernames(rows);
            return hit;
        }

        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<AnnouncementEntity>()
                .eq(AnnouncementEntity::getStatus, STATUS_PUBLISHED)
                .orderByDesc(AnnouncementEntity::getIsTop)
                .orderByDesc(AnnouncementEntity::getPublishTime);


        Page<AnnouncementEntity> page = announcementMapper.selectPage(new Page<>(current, size), wrapper);

        AnnouncementPublishedPageCache toSave = new AnnouncementPublishedPageCache();
        toSave.setRecords(page.getRecords().stream().map(this::toVo).toList());
        toSave.setTotal(page.getTotal());
        toSave.setCurrent(page.getCurrent());
        toSave.setSize(page.getSize());
        toSave.setPages(page.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
        IPage<AnnouncementVO> result = page.convert(this::toVo);
        enrichUsernames(result.getRecords());
        return result;
    }

    /* 校验标题/内容/类型后整行更新，并清除详情缓存 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAnnouncement(Long id, AnnouncementUpdateDTO dto, HttpServletRequest request) {
        AnnouncementEntity entity = announcementMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }
        /* 设置标题、内容、类型、是否置顶 */
        if(!Objects.equals(entity.getCreateUser(), LoginUserContext.getAuthId(request))) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setType(dto.getType());
        entity.setIsTop(dto.getIsTop());
        /* 更新实体并清除详情缓存 */
        updateById(entity);
        evictDetailCache(id);
        bumpPublishedListCacheVersion();
    }

    /** 上下架状态更新 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status, HttpServletRequest request) {
        /* 获取实体并校验 */
        validateStatus(status);
        AnnouncementEntity entity = announcementMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }
        /* 设置状态 */
        entity.setStatus(status);
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
        bumpPublishedListCacheVersion();
    }

    // 读详情：优先缓存；缓存未命中则查库校验、浏览量 +1、写缓存。
    @Override
    public AnnouncementVO getAnnouncement(Long id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);

        // 先拦截空值占位（防穿透）
        if (redisJsonCacheTool.isNullMarker(cacheKey)) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }

        // 缓存命中：DB 原子 +1，并同步返回值与缓存
        AnnouncementVO fromCache = tryLoadAndBumpFromCache(id, cacheKey);
        if (fromCache != null) {
          enrichUsername(fromCache);
            return fromCache;
        }

        // 未命中：查库校验是否存在且已发布
        AnnouncementEntity entity = announcementMapper.selectById(id);
        if (entity == null||!Objects.equals(entity.getStatus(), STATUS_PUBLISHED)) {
            redisJsonCacheTool.setNullMarker(cacheKey);
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }

        // DB 原子 +1（并发安全）
        LambdaUpdateWrapper<AnnouncementEntity> bump = new LambdaUpdateWrapper<AnnouncementEntity>()
                .eq(AnnouncementEntity::getId, id)
                .eq(AnnouncementEntity::getStatus, STATUS_PUBLISHED)
                .setSql("view_count = IFNULL(view_count, 0) + 1");
        int rows = announcementMapper.update(null, bump);
        if (rows <= 0) {
            redisJsonCacheTool.delete(cacheKey);
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }

        // 同步返回值中的浏览量（entity 是更新前的对象）
        int currentViewCount = entity.getViewCount() == null ? 0 : entity.getViewCount();
        entity.setViewCount(currentViewCount + 1);
        AnnouncementVO vo = toVo(entity);
        writeDetailCache(cacheKey, vo);
        enrichUsername(vo);
        return vo;
    }

    /** 缓存命中时，更新浏览量并返回 VO */
    private AnnouncementVO tryLoadAndBumpFromCache(Long id, String cacheKey) {
        // 从缓存中获取VO
        AnnouncementVO vo = redisJsonCacheTool.getObject(cacheKey, AnnouncementVO.class);
        if (vo == null) {
            // 缓存未命中，返回null
            return null;
        }
        // 只允许已发布公告走缓存命中路径；否则删缓存回落 DB
        if (!Objects.equals(vo.getStatus(), STATUS_PUBLISHED)) {
            // 缓存命中但状态不匹配，删除缓存并返回null
            redisJsonCacheTool.delete(cacheKey);
            return null;
        }
        LambdaUpdateWrapper<AnnouncementEntity> bump = new LambdaUpdateWrapper<AnnouncementEntity>()
                .eq(AnnouncementEntity::getId, id)
                .eq(AnnouncementEntity::getStatus, STATUS_PUBLISHED)
                .setSql("view_count = IFNULL(view_count, 0) + 1");
        int rows = announcementMapper.update(null, bump);
        if (rows <= 0) {
            redisJsonCacheTool.delete(cacheKey);
            return null;
        }
        int current = vo.getViewCount() == null ? 0 : vo.getViewCount();
        vo.setViewCount(current + 1);
        // 更新缓存
        writeDetailCache(cacheKey, vo);
        enrichUsername(vo);
        return vo;
    }

    /** 管理员公告详情 */
    @Override
    public AnnouncementVO getAdminAnnouncement(Long id) {
        AnnouncementEntity entity = announcementMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }
        AnnouncementVO vo = toVo(entity);
        enrichUsername(vo);
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
                .orderByDesc(AnnouncementEntity::getViewCount)
                .orderByAsc(AnnouncementEntity::getCreateTime)
                .last("limit " + n);
        /* 转换为 VO 并返回 */
               List<AnnouncementVO> list = announcementMapper.selectList(wrapper).stream().map(this::toVo).toList();
        enrichUsernames(list);
        return list;

    }

    /* 逻辑删除并清理缓存 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnouncement(Long id) {
        /* 删除实体并清除详情缓存 */
        if (!removeById(id)) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }
        evictDetailCache(id);
        bumpPublishedListCacheVersion();
    }

    /* 管理员分页查询公告 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<AnnouncementVO> pageAdmin(
            Long current,
            Long size,
            Integer status,
            String title,
            Integer type,
            Integer isTop,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_ADMIN_PREFIX
                + CacheKeyUtils.listFilterSegment(status, title, type, isTop, startTime, endTime);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        AnnouncementPublishedPageCache cached = redisJsonCacheTool.getObject(listKey, AnnouncementPublishedPageCache.class);
        if (cached != null) {
            List<AnnouncementVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<AnnouncementVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            enrichUsernames(rows);
            return hit;
        }
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<AnnouncementEntity>()
                .eq(status != null, AnnouncementEntity::getStatus, status)
                .like(StringUtils.hasText(title), AnnouncementEntity::getTitle, title)
                .eq(type != null, AnnouncementEntity::getType, type)
                .eq(isTop != null, AnnouncementEntity::getIsTop, isTop)
                .ge(startTime != null, AnnouncementEntity::getPublishTime, startTime)
                .le(endTime != null, AnnouncementEntity::getPublishTime, endTime)
                .orderByDesc(AnnouncementEntity::getUpdateTime);
        Page<AnnouncementEntity> page = announcementMapper.selectPage(new Page<>(current, size), wrapper);
        AnnouncementPublishedPageCache toSave = new AnnouncementPublishedPageCache();
        toSave.setRecords(page.getRecords().stream().map(this::toVo).toList());
        toSave.setTotal(page.getTotal());
        toSave.setCurrent(page.getCurrent());
        toSave.setSize(page.getSize());
        toSave.setPages(page.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
          IPage<AnnouncementVO> result = page.convert(this::toVo);
          enrichUsernames(result.getRecords());
        return result;
    }

    /* 管理员待审核公告 */
    @Override
    public IPage<AnnouncementVO> pagePending(
            Long current,
            Long size,
            String title,
            Integer type,
            Integer isTop,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_PENDING_PREFIX
                + CacheKeyUtils.listFilterSegment(title, type, isTop, startTime, endTime);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        AnnouncementPublishedPageCache cached = redisJsonCacheTool.getObject(listKey, AnnouncementPublishedPageCache.class);
        if (cached != null) {
            List<AnnouncementVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<AnnouncementVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            enrichUsernames(rows);
            return hit;
        }
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<AnnouncementEntity>()
                .eq(AnnouncementEntity::getStatus, STATUS_PENDING)
                .like(StringUtils.hasText(title), AnnouncementEntity::getTitle, title)
                .eq(type != null, AnnouncementEntity::getType, type)
                .eq(isTop != null, AnnouncementEntity::getIsTop, isTop)
                .ge(startTime != null, AnnouncementEntity::getPublishTime, startTime)
                .le(endTime != null, AnnouncementEntity::getPublishTime, endTime)
                .orderByDesc(AnnouncementEntity::getUpdateTime);
        Page<AnnouncementEntity> page = announcementMapper.selectPage(new Page<>(current, size), wrapper);
        AnnouncementPublishedPageCache toSave = new AnnouncementPublishedPageCache();
        toSave.setRecords(page.getRecords().stream().map(this::toVo).toList());
        toSave.setTotal(page.getTotal());
        toSave.setCurrent(page.getCurrent());
        toSave.setSize(page.getSize());
        toSave.setPages(page.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
          IPage<AnnouncementVO> result =page.convert(this::toVo);
          enrichUsernames(result.getRecords());
        return result;
    }

    /* 管理员审核公告 */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void auditAnnouncement(Long id, Integer status, HttpServletRequest request) {
        validateStatus(status);
        AnnouncementEntity entity = announcementMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }
        entity.setAuditTime(LocalDateTime.now());
        entity.setAuditUser(LoginUserContext.getAuthId(request));
        if (Objects.equals(status, STATUS_PUBLISHED)) {
            entity.setPublishTime(LocalDateTime.now());
        }
        entity.setStatus(status);
        updateById(entity);
        evictDetailCache(id);
        bumpPublishedListCacheVersion();
    }

    /* 管理员审核历史列表 */
    @Override
    public IPage<AnnouncementVO> pageAudited(
            Long current,
            Long size,
            String title,
            Integer type,
            Integer isTop,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_AUDITED_PREFIX
                + CacheKeyUtils.listFilterSegment(title, type, isTop, startTime, endTime);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        AnnouncementPublishedPageCache cached = redisJsonCacheTool.getObject(listKey, AnnouncementPublishedPageCache.class);
        if (cached != null) {
            List<AnnouncementVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<AnnouncementVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            enrichUsernames(rows);
            return hit;
        }
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<AnnouncementEntity>()
                .in(AnnouncementEntity::getStatus, 1, 2)
                .like(StringUtils.hasText(title), AnnouncementEntity::getTitle, title)
                .eq(type != null, AnnouncementEntity::getType, type)
                .eq(isTop != null, AnnouncementEntity::getIsTop, isTop)
                .ge(startTime != null, AnnouncementEntity::getPublishTime, startTime)
                .le(endTime != null, AnnouncementEntity::getPublishTime, endTime)
                .orderByDesc(AnnouncementEntity::getAuditTime)
                .orderByDesc(AnnouncementEntity::getUpdateTime);
        Page<AnnouncementEntity> page = announcementMapper.selectPage(new Page<>(current, size), wrapper);
        AnnouncementPublishedPageCache toSave = new AnnouncementPublishedPageCache();
        toSave.setRecords(page.getRecords().stream().map(this::toVo).toList());
        toSave.setTotal(page.getTotal());
        toSave.setCurrent(page.getCurrent());
        toSave.setSize(page.getSize());
        toSave.setPages(page.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
        IPage<AnnouncementVO> result= page.convert(this::toVo);
        enrichUsernames(result.getRecords());
        return result;
    }

    // 绑定已上传的媒体文件 URL（封面或图片列表追加），并清除详情缓存
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindUploadedMedia(Long announcementId, String mediaUrl, String slot,String uploadedFileType, Integer operatorUserId) {
       if(announcementId == null || !StringUtils.hasText(mediaUrl) ||!StringUtils.hasText(uploadedFileType)) {
           throw new BusinessException(ErrorCode.PARAM_MISSING);
          }
          AnnouncementEntity entity = getById(announcementId);
          if(entity == null) {
              throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
          }
          if(!Objects.equals(entity.getCreateUser(), operatorUserId)) {
              throw new BusinessException(ErrorCode.NO_PERMISSION, "只能绑定自己的公告");
          }
          String ft=uploadedFileType.trim().toLowerCase();
          switch(slot.trim().toUpperCase()){
            case "COVER"->{
              if(!"image".equals(ft)){
                  throw new BusinessException(ErrorCode.PARAM_INVALID, "封面图片必须是图片类型");
              }
              entity.setCoverUrl(mediaUrl);
            }
            case "IMAGES_APPEND"->{
              if(!"image".equals(ft)){
                  throw new BusinessException(ErrorCode.PARAM_INVALID, "图片列表只能添加图片类型");
              }
              entity.setImages(appendImagesJson(entity.getImages(), mediaUrl));
            }
            default -> throw new BusinessException(ErrorCode.PARAM_INVALID, "未知的绑定槽位");
          }
          updateById(entity);
          evictDetailCache(announcementId);
          bumpPublishedListCacheVersion();
        }

        //图片列表追加：反序列化旧值（如果有且合法），追加新 URL，序列化回字符串
        private String appendImagesJson(String existing,String newUrl){
          List<String> urls=new ArrayList<>();
          if(StringUtils.hasText(existing)){
            String trimmed=existing.trim();
            try {
                urls.addAll(objectMapper.readValue(trimmed,new TypeReference<List<String>>(){}));
            } catch (JsonProcessingException e) {
              urls.add(trimmed);
            }
          }
          urls.add(newUrl);
          try {
              return objectMapper.writeValueAsString(urls);
          } catch (JsonProcessingException e) {
              throw new BusinessException(ErrorCode.PARAM_INVALID,"图片列表序列化失败");
        }
      }
    /** 序列化 VO 写入 Redis */
    private void writeDetailCache(String cacheKey, AnnouncementVO vo) {
        redisJsonCacheTool.setObject(cacheKey, vo);
    }

    /** 转换为 VO */
    private AnnouncementVO toVo(AnnouncementEntity entity) {
        AnnouncementVO vo = new AnnouncementVO();
        BeanUtils.copyProperties(Objects.requireNonNull(entity), vo);
        return vo;
    }

    /** 使公告各列表分页缓存失效：版本号 +1，读侧使用新前缀 */
    private void bumpPublishedListCacheVersion() {
        redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
    }

    /** 写操作后删除对应详情缓存，避免脏读 */
    private void evictDetailCache(Long id) {
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
    }

    /** 校验状态 */
    private void validateStatus(Integer status) {
        if (status == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "缺少必要参数：status");
        }
        if (!Objects.equals(status, STATUS_PENDING)
                && !Objects.equals(status, STATUS_PUBLISHED)
                && !Objects.equals(status, STATUS_REJECTED)
                && !Objects.equals(status, STATUS_OFFLINE)) {
            throw new BusinessException(ErrorCode.STATUS_INVALID);
        }
    }

     /** 批量填充创建人、审核人姓名 */
     private void enrichUsernames(List<AnnouncementVO> rows){
      /* 收集用户ID */
      if(rows==null||rows.isEmpty()){
        return;
      }
      Set<Integer> ids=new HashSet<>();
      for(AnnouncementVO vo:rows){
        if(vo.getCreateUser()!=null) ids.add(vo.getCreateUser());
        if(vo.getAuditUser()!=null) ids.add(vo.getAuditUser());
      }

      if(ids.isEmpty()){
        return;
      }
      List<AuthEntity> auths = authMapper.selectList(
        new LambdaQueryWrapper<AuthEntity>().in(AuthEntity::getId,ids));

          Map<Integer,String> idToName =new HashMap<>();

          for(AuthEntity a:auths){
            if(a.getId()!=null){
            idToName.put(a.getId(),a.getUsername());
            }
          }
          for(AnnouncementVO vo:rows){
            if(vo.getCreateUser()!=null){
              vo.setCreateUserName(idToName.get(vo.getCreateUser()));
            }
            if(vo.getAuditUser()!=null){
              vo.setAuditUserName(idToName.get(vo.getAuditUser()));
            }
          }
     }

     /* 单条 VO 填充用户名 */
     private void enrichUsername(AnnouncementVO vo){
      if(vo==null) return;
      enrichUsernames(Collections.singletonList(vo));
     }
}
