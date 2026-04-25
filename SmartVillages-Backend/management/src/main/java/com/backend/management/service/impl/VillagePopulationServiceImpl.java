package com.backend.management.service.impl;

import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.management.dto.VillagePopulationCreateDTO;
import com.backend.management.dto.VillagePopulationUpdateDTO;
import com.backend.management.entity.VillagePopulationEntity;
import com.backend.management.mapper.VillagePopulationMapper;
import com.backend.management.service.VillagePopulationService;
import com.backend.management.vo.VillagePopulationDetailVO;
import com.backend.management.vo.VillagePopulationSimpleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class VillagePopulationServiceImpl
        extends ServiceImpl<VillagePopulationMapper, VillagePopulationEntity>
        implements VillagePopulationService {

    
}

