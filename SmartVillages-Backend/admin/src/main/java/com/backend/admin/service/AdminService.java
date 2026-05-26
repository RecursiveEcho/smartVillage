package com.backend.admin.service;

import com.backend.admin.entity.AdminEntity;
import com.backend.auth.dto.AuthDTO;
import com.backend.auth.vo.AuthVO;
import com.backend.auth.vo.CreateCaderVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AdminService extends IService<AdminEntity> {

  /** 查询用户列表。 */
  IPage<AuthVO> pageUsers(String username, String role, Integer status, Long current, Long size);

  /** 更新用户状态。 */
  void updateUserStatus(Integer id, Integer status);

  /** 创建村干部。 */
  CreateCaderVO createCadre(AuthDTO authDTO);

  /** 查看用户详细信息。 */
  AuthVO getUserDetail(Integer id);

  /** 删除用户。 */
  void deleteUser(Integer id);
}
