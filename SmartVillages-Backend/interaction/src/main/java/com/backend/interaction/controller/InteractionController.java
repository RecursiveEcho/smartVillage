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
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.backend.interaction.vo.InteractionCreateVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.PathVariable;
import com.backend.interaction.dto.ReplyInteractionDTO;
/**
 * @author chenyang
 * @date 2026/4/15
 * @description 村民留言控制器
 */
@RestController
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
    @PostMapping("/interactions/messages")
    public Result<InteractionCreateVO> createMessage(@RequestBody @Valid InteractionCreateDTO dto, HttpServletRequest request) {
        return Result.success(interactionService.createMessage(dto, request));
    }

    /**
     * @author chenyang
     * @date 2026/4/15
     * @description 获取村民留言列表
     * @param current 当前页
     * @param size 每页数量
     * @return 村民留言列表
     */
    @Operation(summary="获取村民留言列表")
    @GetMapping("/interactions/messages")
    public Result<IPage<InteractionCreateVO>> getMessageList(@RequestParam(defaultValue = "1") Long current,
    @RequestParam(defaultValue = "10") Long size) {
        return Result.success(interactionService.getMessageList(current, size));
    }


    @Operation(summary="回复村民留言")
    @PostMapping("/cadres/interactions/messages/{id}/replies")
    public Result<String> replyMessage(@PathVariable Long id, @RequestBody @Valid ReplyInteractionDTO dto, HttpServletRequest request) {
        return Result.success(interactionService.replyMessage(id, dto, request));
    }
}