package com.backend.admin.service;

import com.backend.admin.entity.AdminEntity;
import com.backend.admin.vo.AdminVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.auth.dto.AuthDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService extends IService<AdminEntity> {

    // 查询用户列表
    IPage<AdminVO> pageUsers(String username, String role, Integer status, Long current, Long size);

    // 更新用户状态
    void updateUserStatus(Integer id, Integer status);

    // 创建村干部
    void createCadre(AuthDTO authDTO);

    // 上传头像
    void uploadCadreAvatar(Integer id, MultipartFile file, HttpServletRequest request);
}
