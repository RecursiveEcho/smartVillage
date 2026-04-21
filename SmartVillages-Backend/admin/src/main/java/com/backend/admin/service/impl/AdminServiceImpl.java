package com.backend.admin.service.impl;

import ch.qos.logback.core.util.MD5Util;
import com.backend.admin.entity.AdminEntity;
import com.backend.admin.mapper.AdminMapper;
import com.backend.admin.service.AdminService;
import com.backend.admin.vo.AdminVO;
import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.media.vo.UploadVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.backend.media.service.MediaService;
import com.backend.auth.dto.AuthDTO;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import com.backend.common.context.LoginUserContext;
/**
 * 管理员业务实现：继承 MyBatis-Plus 对 {@link AdminEntity} 的基础 CRUD，
 * 用户列表与状态变更实际读写 {@link AuthEntity}（认证账号表），与管理员扩展信息分离。
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminEntity> implements AdminService {

    private final AuthMapper authMapper;

    private final MediaService mediaService;

    /**
     * 按条件分页查询认证用户，并映射为 {@link AdminVO} 返回给管理端。
     * <p>
     * {@code username} 目前仅占位，未参与查询条件；{@code role}、{@code status} 非空时才会过滤。
     * 排序：优先按状态降序、创建时间降序，再按 id 升序，保证列表相对稳定。
     */
    @Override
    public IPage<AdminVO> pageUsers(String username, String role, Integer status, Long current, Long size) {
        Page<AuthEntity> page = new Page<>(current, size);
        // 条件为 null 时不拼进 WHERE，实现「可选筛选」
        LambdaQueryWrapper<AuthEntity> wrapper = new LambdaQueryWrapper<AuthEntity>()
                .eq(role != null, AuthEntity::getRole, role)
                .eq(status != null, AuthEntity::getStatus, status)
                .orderByDesc(AuthEntity::getStatus)
                .orderByDesc(AuthEntity::getCreateTime)
                .orderByAsc(AuthEntity::getId);
        // 查询认证账号表
        IPage<AuthEntity> entityPage = authMapper.selectPage(page, wrapper);

        // 保持与原分页对象一致的页码、条数、总数，仅替换记录类型
        Page<AdminVO> voPage = new Page<>(
                entityPage.getCurrent(),
                entityPage.getSize(),
                entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream().map(this::toAdminVo).toList());
        return voPage;
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
    public void createCadre(AuthDTO authDTO) {
        AuthEntity entity = new AuthEntity();
        String password = DigestUtils.md5DigestAsHex(authDTO.getPassword().getBytes(StandardCharsets.UTF_8));
        entity.setPassword(password);
        BeanUtils.copyProperties(authDTO, entity);
        entity.setRole("cadre");
        entity.setStatus(1);
        entity.setIsDeleted(0);
        authMapper.insert(entity);
    }
    
    /**
     * 上传头像
     * @param id 用户ID
     * @param avatar 头像文件
     * @param request 请求
     * @return 上传结果
     */
    @Override
    public void uploadCadreAvatar(Integer id, MultipartFile avatar, HttpServletRequest request) {
        UploadVO uploadVo = mediaService.upload(avatar, "image", "other", request);
        AuthEntity entity = authMapper.selectById(LoginUserContext.getAuthId(request));
        entity.setAvatar(uploadVo.getFileUrl());
        authMapper.updateById(entity);
    }

    /** 认证实体字段与 VO 同名字段拷贝，供列表展示 */
    private AdminVO toAdminVo(AuthEntity entity) {
        AdminVO vo = new AdminVO();
        // 认证实体字段与 VO 同名字段拷贝
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

}
