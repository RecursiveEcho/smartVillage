package com.backend.management.service;

import com.backend.management.dto.ServiceTicketCreateDTO;
import com.backend.management.entity.VillageServiceTicketEntity;
import com.backend.management.vo.ServiceTicketDetailVO;
import com.backend.management.vo.ServiceTicketSimpleVO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.core.metadata.IPage;

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

    /**
     * 获取民生服务工单列表
     * @param current 当前页
     * @param size 每页条数
     * @param serviceType 服务类型
     * @param status 状态
     * @param request 请求
     * @return 民生服务工单列表
     */
    IPage<ServiceTicketSimpleVO> getServiceTicketList(Long current, Long size, String serviceType, Integer status, HttpServletRequest request);

    /**
     * 获取民生服务工单详情
     * @param id 民生服务工单id
     * @param request 请求
     * @return 民生服务工单详情
     */
    ServiceTicketDetailVO getMyDetail(Long id, HttpServletRequest request);

    /**
     * 取消我的民生服务工单申请
     * @param id 民生服务工单id
     * @param request 请求
     * @return 操作结果文案
     */
    void closeMyTicket(Long id, HttpServletRequest request);
}

