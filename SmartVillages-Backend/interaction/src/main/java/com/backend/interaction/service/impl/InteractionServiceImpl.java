package com.backend.interaction.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import com.backend.interaction.entity.InteractionEntity;
import com.backend.interaction.dto.InteractionCreateDTO;
import com.backend.interaction.mapper.InteractionMapper;
import com.backend.interaction.service.InteractionService;
import com.backend.common.context.LoginUserContext;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author chenyang
 * @date 2026/4/15
 * @description 村民留言业务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class InteractionServiceImpl extends ServiceImpl<InteractionMapper, InteractionEntity> implements InteractionService {
    private final InteractionMapper interactionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMessage(InteractionCreateDTO dto, HttpServletRequest request) {
        InteractionEntity entity = new InteractionEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setUserId(LoginUserContext.getAuthId(request));
        save(entity);
    }
}