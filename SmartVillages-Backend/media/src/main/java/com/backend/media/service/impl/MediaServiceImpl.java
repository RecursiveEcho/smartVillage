package com.backend.media.service.impl;

import com.backend.common.context.LoginUserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.backend.media.service.MediaService;
import com.backend.media.vo.UploadVO;
import com.backend.media.tool.OssUploadTool;
import com.backend.common.exception.BusinessException;
import com.backend.common.enums.ErrorCode;
import java.io.IOException;
import org.springframework.beans.BeanUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.backend.media.vo.DetailVO;
import com.backend.media.mapper.MediaMapper;
import com.backend.media.entity.MediaEntity;
import com.backend.media.dto.MediaListPageCache;
import com.backend.media.vo.PageVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.common.utils.CacheKeyUtils;

import java.util.Collections;
import java.util.List;
@Slf4j
/*
  @author chenyang
 * &#064;date 2026/4/20
 * &#064;description 媒体资源服务实现
 */
@Service
@RequiredArgsConstructor
public class MediaServiceImpl extends ServiceImpl<MediaMapper, MediaEntity> implements MediaService {
    private final OssUploadTool ossUploadTool;
    private final RedisJsonCacheTool redisJsonCacheTool;
    private static final String CACHE_KEY_PREFIX = "media:detail:";
    private static final String CACHE_LIST_VER_KEY = "media:list:ver";
    private static final String CACHE_LIST_PREFIX = "media:list:";
    @Override
    public UploadVO upload(MultipartFile file, String fileType, String category, HttpServletRequest request) {
        
        // 判断文件大小是否超过1GB
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
        UploadVO fromCache = redisJsonCacheTool.getObject(cacheKey, UploadVO.class);
        if (fromCache != null) {
            return fromCache;
        }
        try {
            // 获取当前用户ID
            // 上传文件到OSS
            OssUploadTool.UploadResult uploadResult = ossUploadTool.uploadFile(
                    file.getInputStream(),
                    fileType,
                    file.getOriginalFilename(),
                    file.getContentType()
            );
            
            // 保存媒体资源到数据库
            MediaEntity mediaEntity = new MediaEntity();
            mediaEntity.setFileName(file.getOriginalFilename());
            mediaEntity.setFileUrl(uploadResult.url());
            mediaEntity.setFileType(fileType);
            mediaEntity.setFileSize(file.getSize());
            mediaEntity.setCategory(category);
            mediaEntity.setUploadUser(LoginUserContext.getAuthId(request));
            this.save(mediaEntity);
            bumpListCacheVersion();
            //写入缓存
            redisJsonCacheTool.setObject(cacheKey, new UploadVO(
                file.getOriginalFilename(),
                file.getSize(),
                uploadResult.url(),
                uploadResult.objectKey()));
            return new UploadVO(
                file.getOriginalFilename(),
                file.getSize(),
                uploadResult.url(),
                uploadResult.objectKey());
        } catch (IOException e) {
            log.error("读取上传文件流失败，filename={}, fileType={}", file.getOriginalFilename(), fileType, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }


    /**
     * 分页查询媒体资源
     * @param current 当前页
     * @param size 每页条数
     * @param fileType 文件类型
     * @param category 分类
     * @param status 状态
     * @return 分页查询结果
     */
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
        return entityPage.convert(this::toPageVo);
    }

    /**
     * 删除媒体资源
     * @param id 媒体资源id
     */
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

    /**
     * 获取媒体资源详情
     * @param id 媒体资源id
     * @return 媒体资源详情
     */
    @Override
    public DetailVO getDetail(Integer id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        DetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, DetailVO.class);
        if (fromCache != null) {
            return fromCache;
        }
        MediaEntity mediaEntity = this.getById(id);
        if (mediaEntity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "媒体资源不存在");
        }
        DetailVO detailVO = new DetailVO();
        BeanUtils.copyProperties(mediaEntity, detailVO);
        redisJsonCacheTool.setObject(cacheKey, detailVO);
        return detailVO;
    }

    /**
     * 启用/禁用媒体资源
     * @param id 媒体资源id
     * @param status 状态 0-禁用 1-启用
     */
    @Override
    public void updateStatus(Integer id, Integer status) {
        MediaEntity mediaEntity = this.getById(id);
        if (mediaEntity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "媒体资源不存在");
        }
        mediaEntity.setStatus(status);
        this.updateById(mediaEntity);
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
        bumpListCacheVersion();
    }

    private PageVO toPageVo(MediaEntity entity) {
        PageVO vo = new PageVO();
        BeanUtils.copyProperties(Objects.requireNonNull(entity), vo);
        return vo;
    }

    private void bumpListCacheVersion() {
        redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
    }
}