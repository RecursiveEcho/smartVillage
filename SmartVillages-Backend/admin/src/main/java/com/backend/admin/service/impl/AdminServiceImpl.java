package com.backend.admin.service.impl;

import com.backend.admin.dto.AuthPublishedPageCache;
import com.backend.admin.entity.AdminEntity;
import com.backend.admin.mapper.AdminMapper;
import com.backend.admin.service.AdminService;
import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.auth.vo.AuthVO;
import com.backend.auth.vo.CreateCaderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.backend.media.service.MediaService;
import com.backend.auth.dto.AuthDTO;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Collections;
import java.util.List;

import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.common.utils.CacheKeyUtils;
/**
 * 管理员业务实现：继承 MyBatis-Plus 对 {@link AdminEntity} 的基础 CRUD，
 * 用户列表与状态变更实际读写 {@link AuthEntity}（认证账号表），与管理员扩展信息分离。
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminEntity> implements AdminService {

    private final AuthMapper authMapper;
    private static final String CACHE_KEY_PREFIX = "admin:users:detail:";
    private final MediaService mediaService;

    private final RedisJsonCacheTool redisJsonCacheTool;
    
    private static final String CACHE_LIST_KEY_PREFIX = "admin:users:list:";
    private static final String CACHE_LIST_VER_KEY = "admin:users:ver";

    /**
     * 按条件分页查询认证用户，并映射为 {@link AuthVO 返回给管理端。
     * <p>
     * {@code username} 目前仅占位，未参与查询条件；{@code role}、{@code status} 非空时才会过滤。
     * 排序：优先按状态降序、创建时间降序，再按 id 升序，保证列表相对稳定。
     */
    @Override
    public IPage<AuthVO> pageUsers(String username, String role, Integer status, Long current, Long size) {
        String ver = redisJsonCacheTool.getListCacheVersionOrZero(CACHE_LIST_VER_KEY);
        String prefix = CACHE_LIST_KEY_PREFIX + CacheKeyUtils.listFilterSegment(username, role, status);
        String listKey = redisJsonCacheTool.buildVersionedListPageKey(prefix, ver, current, size);
        AuthPublishedPageCache cached = redisJsonCacheTool.getObject(listKey, AuthPublishedPageCache.class);
        if (cached != null) {
            List<AuthVO> rows = cached.getRecords() != null ? cached.getRecords() : Collections.emptyList();
            Page<AuthVO> hit = new Page<>(cached.getCurrent(), cached.getSize(), cached.getTotal());
            hit.setRecords(rows);
            return hit;
        }

        LambdaQueryWrapper<AuthEntity> wrapper = new LambdaQueryWrapper<AuthEntity>()
                .like(StringUtils.hasText(username), AuthEntity::getUsername, username)
                .eq(role != null, AuthEntity::getRole, role)
                .eq(status != null, AuthEntity::getStatus, status)
                .orderByDesc(AuthEntity::getStatus)
                .orderByDesc(AuthEntity::getCreateTime)
                .orderByAsc(AuthEntity::getId);
        // 查询认证账号表
        IPage<AuthEntity> entityPage = authMapper.selectPage(new Page<>(current, size), wrapper);

        // 保持与原分页对象一致的页码、条数、总数，仅替换记录类型
        AuthPublishedPageCache toSave = new AuthPublishedPageCache();
        toSave.setRecords(entityPage.getRecords().stream().map(this::toVo).toList());
        toSave.setTotal(entityPage.getTotal());
        toSave.setCurrent(entityPage.getCurrent());
        toSave.setSize(entityPage.getSize());
        toSave.setPages(entityPage.getPages());
        redisJsonCacheTool.setListCacheObject(listKey, toSave);
        return entityPage.convert(this::toVo);
    }

    /**
     * 校验用户存在且未逻辑删除后，更新其启用状态。
     */
    @Override
    public void updateUserStatus(Integer id, Integer status) {
        AuthEntity entity = authMapper.selectById(id);
        // 用户不存在
        if (entity == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        // 用户已逻辑删除
        if (Objects.equals(entity.getIsDeleted(), 1)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        entity.setStatus(status);
        authMapper.updateById(entity);
    }

    /**
     * 创建村干部账号
     * @param authDTO 用户认证DTO
     */
    @Override
    @SuppressWarnings("null")
    public CreateCaderVO createCadre(AuthDTO authDTO) {
        AuthEntity entity = new AuthEntity();
        String password = DigestUtils.md5DigestAsHex(authDTO.getPassword().getBytes(StandardCharsets.UTF_8));
        BeanUtils.copyProperties(authDTO, entity);
        entity.setPassword(password);
        entity.setRole("cadre");
        entity.setStatus(1);
        entity.setAvatar(authDTO.getAvatar());
        authMapper.insert(entity);
        CreateCaderVO createCaderVO = new CreateCaderVO();
        BeanUtils.copyProperties(entity, createCaderVO);
        return createCaderVO;
    }
    

    

    /**
     * 查看用户详细信息
     * @param id 用户 ID
     * @return 用户详细信息
     */
    @Override
    public AuthVO getUserDetail(Integer id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        if (redisJsonCacheTool.isNullMarker(cacheKey)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        AuthVO fromCache = redisJsonCacheTool.getObject(cacheKey, AuthVO.class);
        if (fromCache != null) {
            return fromCache;
        }
        AuthEntity entity = authMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        AuthVO vo = new AuthVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        return vo;
    }
    /**
     * 写操作后删除对应详情缓存，避免脏读
     * @param id 用户 ID
     */
    private void evictDetailCache(Integer id) {
        redisJsonCacheTool.delete(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id));
    }

    /**
     * 转换为 VO
     * @param entity 实体
     * @return VO
     */
    private AuthVO toVo(AuthEntity entity) {
        AuthVO vo = new AuthVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
    /**
     * 删除用户
     * @param id 用户 ID
     */
    @Override
    public void deleteUser(Integer id) {
        authMapper.deleteById(id);
        evictDetailCache(id);
        redisJsonCacheTool.bumpListCacheVersion(CACHE_LIST_VER_KEY);
    }
}
