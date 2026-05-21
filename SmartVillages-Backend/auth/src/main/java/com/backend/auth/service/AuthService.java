package com.backend.auth.service;

import com.backend.auth.entity.AuthEntity;
import com.backend.auth.vo.JwtResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AuthService extends IService<AuthEntity> {

    // 登录
    JwtResponse login(String username, String password);

    // 登出
    String logout();

    // 上传绑定头像
    void bindUploadedMedia(Long userId, String slot, String mediaUrl,
                           String uploadedFileType, Integer operatorUserId);
}
