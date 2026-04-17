package com.backend.interaction.controller;

import com.backend.interaction.service.InteractionService;
import com.backend.common.result.Result;
import com.backend.interaction.dto.InteractionCreateDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.backend.interaction.vo.InteractionCreateVO;
/**
 * @author chenyang
 * @date 2026/4/15
 * @description 村民留言控制器
 */
@RestController
@RequestMapping("interactions")
@RequiredArgsConstructor
@Tag(name = "村民留言", description = "村民留言接口")
public class InteractionController {

    private final InteractionService interactionService;

    /**
     * @author chenyang
     * @date 2026/4/15
     * @description 新增村民留言
     * @param dto 村民留言创建DTO
     * @return 操作结果文案
     */
    @Operation(summary = "新增村民留言")
    @PostMapping("/messages")
    public Result<InteractionCreateVO> createMessage(@RequestBody @Valid InteractionCreateDTO dto, HttpServletRequest request) {
        return Result.success(interactionService.createMessage(dto, request));
    }

}