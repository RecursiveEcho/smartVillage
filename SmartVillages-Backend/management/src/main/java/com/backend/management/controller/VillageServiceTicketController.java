package com.backend.management.controller;

import com.backend.common.result.Result;
import com.backend.management.dto.ServiceTicketCreateDTO;
import com.backend.management.service.VillageServiceTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.backend.management.vo.ServiceTicketDetailVO;
import com.backend.management.vo.ServiceTicketSimpleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.time.LocalDateTime;
import com.backend.management.dto.ServiceTicketDoneDTO;
/**
 * @author chenyang
 * &#064;date 2026/4/24
 * &#064;description 民生服务工单控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "村务管理-民生服务", description = "民生诉求/服务工单")
public class VillageServiceTicketController {

    private final VillageServiceTicketService villageServiceTicketService;

    /**
     * 村民提交民生服务工单
     * @param dto 民生服务工单创建DTO
     * @param request 请求
     * @return 操作结果文案
     */
    @Operation(summary = "村民提交民生服务工单")
    @PostMapping("/villager/management/services")
    public Result<Integer> create(@Valid @RequestBody ServiceTicketCreateDTO dto, HttpServletRequest request) {
        return Result.success(villageServiceTicketService.create(dto, request));
    }

    /**
     * 获取民生服务工单列表
     * @param current 当前页
     * @param size 每页条数
     * @param serviceType 服务类型
     * @param status 状态
     * @param request 请求
     * @return 民生服务工单列表
     */
    @Operation(summary = "获取民生服务工单列表")
    @GetMapping("/villager/management/services/my")
    public Result<IPage<ServiceTicketSimpleVO>> getMyServiceTicketList(
        @RequestParam(defaultValue = "1") Long current, 
        @RequestParam(defaultValue = "10") Long size, 
        @RequestParam(required = false) String serviceType, 
        @RequestParam(required = false) Integer status, 
        HttpServletRequest request) {
        return Result.success(villageServiceTicketService.getServiceTicketList(current, size, serviceType, status, request));
    }

    /**
     * 获取我的民生服务工单详情
     * @param id 民生服务工单id
     * @param request 请求
     * @return 我的民生服务工单详情
     */
    @Operation(summary = "获取我的民生服务工单详情")
    @GetMapping("/villager/management/services/my/{id}")
    public Result<ServiceTicketDetailVO> getMyDetail(@PathVariable Long id, HttpServletRequest request) {
        return Result.success(villageServiceTicketService.getMyDetail(id, request));
    }

    /**
     * 取消我的民生服务工单申请
     * @param id 民生服务工单id
     * @param request 请求
     * @return 操作结果文案
     */
    @Operation(summary = "取消我的民生服务工单申请")
    @PostMapping("/villager/management/services/my/{id}/close")
    public Result<String> closeMyTicket(@PathVariable Long id, HttpServletRequest request) {
        villageServiceTicketService.closeMyTicket(id, request);
        return Result.success("取消成功");
    }

    /**
     * 管理端获取民生服务工单列表
     * @param current 当前页
     * @param size 每页条数
     * @param serviceType 服务类型
     * @param status 状态
     * @return 民生服务工单列表
     */
    @Operation(summary ="管理端获取民生服务工单列表")
    @GetMapping("/cadre/management/services")
    public Result<IPage<ServiceTicketSimpleVO>> pageCadre(
        @RequestParam(defaultValue = "1") Long current, 
        @RequestParam(defaultValue = "10") Long size, 
        @RequestParam(required = false) String serviceType, 
        @RequestParam(required = false) Integer status, 
        @RequestParam(required = false) LocalDateTime starTime,
        @RequestParam(required = false) LocalDateTime endTime) {
        return Result.success(villageServiceTicketService.pageCadre(current, size, serviceType, status, starTime, endTime));
    }

    /**
     * 管理端获取民生服务工单详情
     * @param id 民生服务工单id
     * @return 民生服务工单详情
     */
    @Operation(summary ="管理端获取民生服务工单详情")
    @GetMapping("/cadre/management/services/{id}")
    public Result<ServiceTicketDetailVO> getServiceTicketDetail(@PathVariable Long id) {
        return Result.success(villageServiceTicketService.getServiceTicketDetail(id));
    }

    /**
     * 管理端处理民生服务工单申请
     * @param id 民生服务工单id
     * @param dto 民生服务工单处理DTO
     * @param request 请求
     * @return 操作结果文案
     */
    @Operation(summary ="管理端处理民生服务工单申请")
    @PutMapping("/cadre/management/services/{id}/processing")
    public Result<String> processingServiceTicket(@PathVariable Long id, @RequestBody ServiceTicketDoneDTO dto, HttpServletRequest request) {
        villageServiceTicketService.processingServiceTicket(id, dto, request);
        return Result.success("处理成功");
    }

    /**
     * 管理端办结民生服务工单申请
     * @param id 民生服务工单id
     * @param request 请求
     * @return 操作结果文案
     */
    @Operation(summary ="管理端办结民生服务工单申请")
    @PutMapping("/cadre/management/services/{id}/done")
    public Result<String> doneServiceTicket(@PathVariable Long id, HttpServletRequest request) {
        villageServiceTicketService.doneServiceTicket(id, request);
        return Result.success("办结成功");
    }
}


