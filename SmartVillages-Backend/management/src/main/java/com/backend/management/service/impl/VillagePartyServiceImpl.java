package com.backend.management.service.impl;

import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.utils.CacheKeyUtils;
import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.management.dto.VillagePartyCreateDTO;
import com.backend.management.dto.VillagePartyUpdateDTO;
import com.backend.management.entity.VillagePartyEntity;
import com.backend.management.mapper.VillagePartyMapper;
import com.backend.management.service.VillagePartyService;
import com.backend.management.vo.VillagePartyDetailVO;
import com.backend.management.vo.VillagePartySimpleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VillagePartyServiceImpl
        extends ServiceImpl<VillagePartyMapper, VillagePartyEntity>
        implements VillagePartyService {

    private static final String CACHE_KEY_PREFIX = "village_party:";
    private final RedisJsonCacheTool redisJsonCacheTool;

    
}

