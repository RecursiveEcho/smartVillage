package com.backend.media.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.common.context.LoginUserContext;
import com.backend.common.enums.ErrorCode;
import com.backend.common.event.MediaBindAfterUploadEvent;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.media.dto.MediaListPageCache;
import com.backend.media.entity.MediaEntity;
import com.backend.media.mapper.MediaMapper;
import com.backend.media.service.MediaService;
import com.backend.media.tool.OssUploadTool;
import com.backend.media.vo.DetailVO;
import com.backend.media.vo.PageVO;
import com.backend.media.vo.UploadVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl extends ServiceImpl<MediaMapper, MediaEntity> implements MediaService {
    private final OssUploadTool ossUploadTool;
    private final RedisJsonCacheTool redisJsonCacheTool;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthMapper authMapper;
    private static final String CACHE_KEY_PREFIX = "media:detail:";
    private static final String CACHE_LIST_VER_KEY = "media:list:ver";
    private static final String CACHE_LIST_PREFIX = "media:list:";

    private static final Integer STATUS_PENDING = 0;
    private static final Integer STATUS_APPROVED = 1;
    private static final Integer STATUS_REJECTED = 2;
    private static final Integer STATUS_OFFLINE = 3;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadVO upload(
            MultipartFile file,
            String fileType,
            String category,
            String usageRemark,
            String bindTarget,
            Long bindEntityId,
            String bindSlot,
            HttpServletRequest request) {

        String remark = normalizeUsageRemark(usageRemark);

        boolean anyBind = StringUtils.hasText(bindTarget) || bindEntityId != null || StringUtils.hasText(bindSlot);
        boolean fullBind =
                StringUtils.hasText(bindTarget) && bindEntityId != null && StringUtils.hasText(bindSlot);

        if (anyBind && !fullBind) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "bindTarget、bindEntityId、bindSlot 需同时填写");
        }

        if (file.getSize() > 1024 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "文件大小不能超过1GB");
        }
        if (file.getSize() == 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "文件大小不能为0");
        }
        if (file.getOriginalFilename() == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "文件名不能为空");
        }
        if (file.getContentType() == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "文件类型不能为空");
        }

        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, file.getOriginalFilename());

        if (!fullBind) {
            UploadVO fromCache = redisJsonCacheTool.getObject(cacheKey, UploadVO.class);
            if (fromCache != null) {
                return fromCache;
            }
        }
        try {
            OssUploadTool.UploadResult uploadResult = ossUploadTool.uploadFile(
                    file.getInputStream(),
                    fileType,
                    file.getOriginalFilename(),
                    file.getContentType()
            );

            MediaEntity mediaEntity = new MediaEntity();
            mediaEntity.setFileName(file.getOriginalFilename());
            mediaEntity.setFileUrl(uploadResult.url());
            mediaEntity.setFileType(fileType);
            mediaEntity.setFileSize(file.getSize());
            mediaEntity.setCategory(category);
            mediaEntity.setUsageRemark(remark);
            Integer uid = LoginUserContext.getAuthId(request);
            mediaEntity.setUploadUser(uid);
            mediaEntity.setStatus(STATUS_PENDING);
            if (fullBind) {
                mediaEntity.setBindTarget(bindTarget.trim());
                mediaEntity.setBindEntityId(bindEntityId);
                mediaEntity.setBindSlot(bindSlot.trim());
            }
            this.save(mediaEntity);
            bumpListCacheVersion();
            UploadVO cachedVo = buildUploadVo(file, uploadResult, remark, uid);
            if (!fullBind) {
                redisJsonCacheTool.setObject(cacheKey, cachedVo);
            }
            return cachedVo;
        } catch (IOException e) {
            log.error("读取上传文件流失败，filename={}, fileType={}", file.getOriginalFilename(), fileType, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public IPage<PageVO> page(Long current, Long size, String fileType, String category, Integer status) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_PREFIX + CacheKeyUtils.listFilterSegment(fileType, category, status);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        MediaListPageCache cached = redisJsonCacheTool.getObject(listKey, MediaListPageCache.class);
        if (cached != null) {
            List<PageVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<PageVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            enrichPageUsernames(rows);
            return hit;
        }
        LambdaQueryWrapper<MediaEntity> wrapper = new LambdaQueryWrapper<MediaEntity>()
            .eq(fileType != null, MediaEntity::getFileType, fileType)
            .eq(category != null, MediaEntity::getCategory, category)
            .eq(status != null, MediaEntity::getStatus, status)
            .orderByDesc(MediaEntity::getCreateTime);
        IPage<MediaEntity> entityPage = page(new Page<>(current, size), wrapper);
        MediaListPageCache toSave = new MediaListPageCache();
        toSave.setRecords(entityPage.getRecords().stream().map(this::toPageVo).toList());
        toSave.setTotal(entityPage.getTotal());
        toSave.setCurrent(entityPage.getCurrent());
        toSave.setSize(entityPage.getSize());
        toSave.setPages(entityPage.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
        IPage<PageVO> result = entityPage.convert(this::toPageVo);
        enrichPageUsernames(result.getRecords());
        return result;
    }

    @Override
    public void delete(Integer id) {
        MediaEntity mediaEntity = this.getById(id);
        if (mediaEntity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "媒体资源不存在");
        }
        this.removeById(id);
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
        bumpListCacheVersion();
    }

    @Override
    public DetailVO getDetail(Integer id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        DetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, DetailVO.class);
        if (fromCache != null) {
            enrichDetailUsername(fromCache);
            return fromCache;
        }
        MediaEntity mediaEntity = this.getById(id);
        if (mediaEntity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "媒体资源不存在");
        }
        DetailVO detailVO = new DetailVO();
        BeanUtils.copyProperties(mediaEntity, detailVO);
        redisJsonCacheTool.setObject(cacheKey, detailVO);
        enrichDetailUsername(detailVO);
        return detailVO;
    }

    @Override
    public void updateStatus(Integer id, Integer status, HttpServletRequest request) {
        MediaEntity mediaEntity = this.getById(id);
        if (mediaEntity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "媒体资源不存在");
        }
        mediaEntity.setStatus(status);
        mediaEntity.setAuditTime(LocalDateTime.now());
        mediaEntity.setAuditUser(LoginUserContext.getAuthId(request));
        this.updateById(mediaEntity);
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
        bumpListCacheVersion();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditMedia(Integer id, Integer status, HttpServletRequest request) {
        if (status == null || (!Objects.equals(status, STATUS_APPROVED) && !Objects.equals(status, STATUS_REJECTED))) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "审核状态只能为1（通过）或2（拒绝）");
        }
        MediaEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.MEDIA_NOT_FOUND);
        }
        entity.setAuditTime(LocalDateTime.now());
        entity.setAuditUser(LoginUserContext.getAuthId(request));
        entity.setStatus(status);
        this.updateById(entity);
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
        bumpListCacheVersion();

        if (Objects.equals(status, STATUS_APPROVED)
                && StringUtils.hasText(entity.getBindTarget())
                && entity.getBindEntityId() != null
                && StringUtils.hasText(entity.getBindSlot())) {
            eventPublisher.publishEvent(
                    new MediaBindAfterUploadEvent(
                            this,
                            entity.getUploadUser(),
                            entity.getFileUrl(),
                            entity.getFileType(),
                            entity.getBindTarget(),
                            entity.getBindEntityId(),
                            entity.getBindSlot()));
        }
    }

    @Override
    public IPage<PageVO> pagePending(Long current, Long size, String fileType, String category) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_PREFIX + CacheKeyUtils.listFilterSegment(fileType, category, STATUS_PENDING);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        MediaListPageCache cached = redisJsonCacheTool.getObject(listKey, MediaListPageCache.class);
        if (cached != null) {
            List<PageVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<PageVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            enrichPageUsernames(rows);
            return hit;
        }
        LambdaQueryWrapper<MediaEntity> wrapper = new LambdaQueryWrapper<MediaEntity>()
            .eq(fileType != null, MediaEntity::getFileType, fileType)
            .eq(category != null, MediaEntity::getCategory, category)
            .eq(MediaEntity::getStatus, STATUS_PENDING)
            .orderByDesc(MediaEntity::getCreateTime);
        IPage<MediaEntity> entityPage = page(new Page<>(current, size), wrapper);
        MediaListPageCache toSave = new MediaListPageCache();
        toSave.setRecords(entityPage.getRecords().stream().map(this::toPageVo).toList());
        toSave.setTotal(entityPage.getTotal());
        toSave.setCurrent(entityPage.getCurrent());
        toSave.setSize(entityPage.getSize());
        toSave.setPages(entityPage.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
        IPage<PageVO> result = entityPage.convert(this::toPageVo);
        enrichPageUsernames(result.getRecords());
        return result;
    }

    @Override
    public IPage<PageVO> pageAudited(Long current, Long size, String fileType, String category) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_PREFIX + CacheKeyUtils.listFilterSegment(fileType, category, null) + "_audited";
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        MediaListPageCache cached = redisJsonCacheTool.getObject(listKey, MediaListPageCache.class);
        if (cached != null) {
            List<PageVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<PageVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            enrichPageUsernames(rows);
            return hit;
        }
        LambdaQueryWrapper<MediaEntity> wrapper = new LambdaQueryWrapper<MediaEntity>()
            .eq(fileType != null, MediaEntity::getFileType, fileType)
            .eq(category != null, MediaEntity::getCategory, category)
            .in(MediaEntity::getStatus, List.of(STATUS_APPROVED, STATUS_REJECTED))
            .orderByDesc(MediaEntity::getAuditTime);
        IPage<MediaEntity> entityPage = page(new Page<>(current, size), wrapper);
        MediaListPageCache toSave = new MediaListPageCache();
        toSave.setRecords(entityPage.getRecords().stream().map(this::toPageVo).toList());
        toSave.setTotal(entityPage.getTotal());
        toSave.setCurrent(entityPage.getCurrent());
        toSave.setSize(entityPage.getSize());
        toSave.setPages(entityPage.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
        IPage<PageVO> result = entityPage.convert(this::toPageVo);
        enrichPageUsernames(result.getRecords());
        return result;
    }

    private PageVO toPageVo(MediaEntity entity) {
        PageVO vo = new PageVO();
        BeanUtils.copyProperties(Objects.requireNonNull(entity), vo);
        return vo;
    }

    private static String normalizeUsageRemark(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String t = raw.trim();
        if (t.length() > 200) {
            return t.substring(0, 200);
        }
        return t;
    }

    private static UploadVO buildUploadVo(
            MultipartFile file,
            OssUploadTool.UploadResult uploadResult,
            String usageRemark,
            Integer uploadUserId) {
        UploadVO vo = new UploadVO();
        vo.setFileName(file.getOriginalFilename());
        vo.setFileSize(file.getSize());
        vo.setFileUrl(uploadResult.url());
        vo.setObjectKey(uploadResult.objectKey());
        vo.setUsageRemark(usageRemark);
        vo.setUploadUserId(uploadUserId);
        return vo;
    }

    private void bumpListCacheVersion() {
        redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
    }

    private void enrichPageUsernames(List<PageVO> rows) {
        if (rows == null || rows.isEmpty()) return;
        Set<Integer> ids = new HashSet<>();
        for (PageVO vo : rows) {
            if (vo.getUploadUser() != null) ids.add(vo.getUploadUser());
            if (vo.getAuditUser() != null) ids.add(vo.getAuditUser());
        }
        if (ids.isEmpty()) return;
        List<AuthEntity> auths = authMapper.selectList(
                new LambdaQueryWrapper<AuthEntity>().in(AuthEntity::getId, ids));
        Map<Integer, String> idToName = new HashMap<>();
        for (AuthEntity a : auths) {
            if (a.getId() != null) idToName.put(a.getId(), a.getUsername());
        }
        for (PageVO vo : rows) {
            if (vo.getUploadUser() != null) vo.setUploadUserName(idToName.get(vo.getUploadUser()));
            if (vo.getAuditUser() != null) vo.setAuditUserName(idToName.get(vo.getAuditUser()));
        }
    }

    private void enrichDetailUsername(DetailVO vo) {
        if (vo == null) return;
        Set<Integer> ids = new HashSet<>();
        if (vo.getUploadUser() != null) ids.add(vo.getUploadUser());
        if (vo.getAuditUser() != null) ids.add(vo.getAuditUser());
        if (ids.isEmpty()) return;
        List<AuthEntity> auths = authMapper.selectList(
                new LambdaQueryWrapper<AuthEntity>().in(AuthEntity::getId, ids));
        Map<Integer, String> idToName = new HashMap<>();
        for (AuthEntity a : auths) {
            if (a.getId() != null) idToName.put(a.getId(), a.getUsername());
        }
        if (vo.getUploadUser() != null) vo.setUploadUserName(idToName.get(vo.getUploadUser()));
        if (vo.getAuditUser() != null) vo.setAuditUserName(idToName.get(vo.getAuditUser()));
    }
}
