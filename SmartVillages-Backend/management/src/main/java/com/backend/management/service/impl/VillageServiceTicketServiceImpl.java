package com.backend.management.service.impl;

import com.backend.common.context.LoginUserContext;
import com.backend.management.dto.ServiceTicketCreateDTO;
import com.backend.management.entity.VillageServiceTicketEntity;
import com.backend.management.mapper.VillageServiceTicketMapper;
import com.backend.management.service.VillageServiceTicketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VillageServiceTicketServiceImpl
        extends ServiceImpl<VillageServiceTicketMapper, VillageServiceTicketEntity>
        implements VillageServiceTicketService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer create(ServiceTicketCreateDTO dto, HttpServletRequest request) {
        VillageServiceTicketEntity entity = new VillageServiceTicketEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setApplicantId(LoginUserContext.getAuthId(request));
        entity.setStatus(0);
        save(entity);
        return entity.getId();
    }
}

