package com.backend.management.service;

import com.backend.management.dto.ServiceTicketCreateDTO;
import com.backend.management.entity.VillageServiceTicketEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author chenyang
 * &#064;date 2026/4/24
 * &#064;description 民生服务工单服务
 */
public interface VillageServiceTicketService extends IService<VillageServiceTicketEntity> {

    /**
     * 创建民生服务工单
     * @param dto 民生服务工单创建DTO
     * @param request 请求
     * @return 民生服务工单ID
     */
    Integer create(ServiceTicketCreateDTO dto, HttpServletRequest request);

    

}

