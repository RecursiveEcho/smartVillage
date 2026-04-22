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
import com.backend.interaction.vo.InteractionCreateVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.LocalDateTime;
import com.backend.interaction.dto.ReplyInteractionDTO;
import com.backend.interaction.vo.InteractionDetailVO;
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

    /* 新增村民留言 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InteractionCreateVO createMessage(InteractionCreateDTO dto, HttpServletRequest request) {
        InteractionEntity entity = new InteractionEntity();
        entity.setUserId(LoginUserContext.getAuthId(request));
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        InteractionCreateVO vo = new InteractionCreateVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /* 获取村民留言列表 */
    @Override
    public IPage<InteractionCreateVO> getMessageList(Long current, Long size) {
        LambdaQueryWrapper<InteractionEntity> wrapper = new LambdaQueryWrapper<InteractionEntity>()
        .orderByDesc(InteractionEntity::getCreateTime);
        IPage<InteractionEntity> entityPage = page(new Page<>(current, size), wrapper);
        return entityPage.convert(entity -> {
            InteractionCreateVO vo = new InteractionCreateVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }

    /* 回复村民留言 */
    @Override
    public String replyMessage(Long id, ReplyInteractionDTO dto, HttpServletRequest request) {
        InteractionEntity entity = getById(id);
        entity.setReply(dto.getReply());
        entity.setReplyTime(LocalDateTime.now());
        entity.setReplyUser(LoginUserContext.getAuthId(request));
        entity.setStatus(2);
        updateById(entity);
        return "回复成功";
    }

    /* 获取村民留言详情 */
    @Override
    public InteractionDetailVO getMessageDetail(Long id) {
        InteractionEntity entity = getById(id);
        InteractionDetailVO vo = new InteractionDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /* 管理端获取村民留言列表 */
    @Override
    public IPage<InteractionDetailVO> getMessageListByCadre(Long current, Long size, Integer status, String type) {
        LambdaQueryWrapper<InteractionEntity> wrapper = new LambdaQueryWrapper<InteractionEntity>()
        .eq(status != null, InteractionEntity::getStatus, status)
        .eq(type != null, InteractionEntity::getType, type)
        .orderByDesc(InteractionEntity::getCreateTime);
        IPage<InteractionEntity> entityPage = page(new Page<>(current, size), wrapper);
        return entityPage.convert(entity -> {
            InteractionDetailVO vo = new InteractionDetailVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }
}