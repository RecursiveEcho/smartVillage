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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}

