package com.backend.admin.service;

import com.backend.admin.entity.AdminEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.auth.dto.AuthDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import com.backend.media.vo.UploadVO;
import com.backend.auth.vo.AuthVO;

public interface AdminService extends IService<AdminEntity> {

    // 查询用户列表
    IPage<AuthVO> pageUsers(String username, String role, Integer status, Long current, Long size);

    // 更新用户状态
    void updateUserStatus(Integer id, Integer status);

    // 创建村干部
    Integer createCadre(AuthDTO authDTO);

    // 上传头像
    UploadVO uploadCadreAvatar(Integer id, MultipartFile file, HttpServletRequest request);
}
