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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.backend.interaction.dto.ReplyInteractionDTO;
import com.backend.interaction.vo.InteractionDetailVO;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
/**
 * @author chenyang
 * &#064;date 2026/4/15
 * &#064;description 村民留言控制器
 */
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "村民留言", description = "村民留言接口")
public class InteractionController {

    private final InteractionService interactionService;

    /**
     * @author chenyang
     * &#064;date 2026/4/15
     * &#064;description 新增村民留言
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
     * &#064;date 2026/4/15
     * &#064;description 获取村民留言列表
     * @param current 当前页
     * @param size 每页数量
     * @return 村民留言列表
     */
    @Operation(summary="获取村民留言列表")
    @GetMapping("/interactions/messages")
    public Result<IPage<InteractionCreateVO>> getMessageList(
    @RequestParam(defaultValue = "1") Long current,
    @RequestParam(defaultValue = "10") Long size) {
        return Result.success(interactionService.getMessageList(current, size));
    }


    /**
     * @author chenyang
     * &#064;date 2026/4/22
     * &#064;description 回复村民留言
     * @param id 留言id
     * @param dto 回复村民留言DTO
     * @param request HTTP请求
     * @return 操作结果文案
     */
    @Operation(summary="回复村民留言")
    @PutMapping("/cadre/interactions/messages/{id}/replies")
    public Result<String> replyMessage(
        @PathVariable Long id, 
        @RequestBody @Valid ReplyInteractionDTO dto, HttpServletRequest request) {
        return Result.success(interactionService.replyMessage(id, dto, request));
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/22
     * &#064;description 获取村民留言详情
     * @param id 留言id
     * @return 村民留言详情
     */
    @Operation(summary="获取村民留言详情")
    @GetMapping("/cadre/interactions/messages/{id}")
    public Result<InteractionDetailVO> getMessageDetail(@PathVariable Long id) {
        return Result.success(interactionService.getMessageDetail(id));
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/22
     * &#064;description 管理端获取村民留言列表
     * @param current 当前页
     * @param size 每页数量
     * @param status 状态
     * @param type 类型
     * @return 管理端村民留言列表
     */
    @Operation(summary="管理端获取村民留言列表")
    @GetMapping("/cadre/interactions/messages")
    public Result<IPage<InteractionDetailVO>> getMessageListByCadre(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startTime,
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endTime
        ) {
        return Result.success(interactionService.getMessageListByCadre(current, size, status, type, startTime, endTime));
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/22
     * &#064;description 我的留言
     * @param current 当前页
     * @param size 每页数量
     * @return 我的留言
     */
    @Operation(summary="我的留言")
    @GetMapping("/interactions/messages/my")
    public Result<IPage<InteractionDetailVO>> getMyMessageList(
    HttpServletRequest request,
    @RequestParam(defaultValue = "1") Long current,
    @RequestParam(defaultValue = "10") Long size) {
        return Result.success(interactionService.getMyMessageList(request, current, size));
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/22
     * &#064;description 我的留言详细
     * @param request HTTP请求
     * @param id 留言id
     * @return 我的留言详细
     */
    @Operation(summary="我的留言详细")
    @GetMapping("/interactions/messages/my/{id}")
    public Result<InteractionDetailVO> getMyMessageDetail(HttpServletRequest request,
                                                         @PathVariable Long id) {
        return Result.success(interactionService.getMyMessageDetail(request, id));
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/22
     * &#064;description 村民撤回留言
     * @param request HTTP请求
     * @param id 留言id
     * @return 操作结果文案
     */
    @Operation(summary="村民撤回留言")
    @PostMapping("/interactions/messages/my/{id}/withdraw")
    public Result<String> withdrawMessage(HttpServletRequest request, @PathVariable Long id) {
        return Result.success(interactionService.withdrawMessage(request, id));
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/22
     * &#064;description 管理端处理村民留言
     * @param id 留言id
     * @param request HTTP请求
     * @return 操作结果文案
     */
    @Operation(summary="管理端处理村民留言")
    @PostMapping("/cadre/interactions/messages/{id}/processing")
    public Result<String> processingMessage(@PathVariable Long id, HttpServletRequest request) {
        return Result.success(interactionService.processingMessage(id, request));
    }



}