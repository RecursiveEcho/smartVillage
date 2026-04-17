package com.backend.interaction.service;

import com.backend.interaction.entity.InteractionEntity;
import com.backend.interaction.dto.InteractionCreateDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import com.backend.interaction.vo.InteractionCreateVO;
import io.swagger.v3.oas.annotations.tags.Tag;
/**
 * @author chenyang
 * @date 2026/4/15
 * @description 村民留言业务接口
 */
@Tag(name = "村民留言", description = "村民留言接口")
public interface InteractionService extends IService<InteractionEntity> {

    // 新增村民留言
    InteractionCreateVO createMessage(InteractionCreateDTO dto, HttpServletRequest request);
}