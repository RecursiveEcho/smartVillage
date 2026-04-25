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

    /**
     * 创建党组织
     * @param dto 党组织创建DTO
     * @return 党组织ID
     */
    @Override
    public  Integer create(VillagePartyCreateDTO dto){
        VillagePartyEntity entity=new VillagePartyEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        Integer id=entity.getId();
        redisJsonCacheTool.setObject(CACHE_KEY_PREFIX, id);
        return id;
    }
    
    /**
     * 分页查询党建组织信息列表
     * @param current 当前页
     * @param size 每页条数
     * @param orgName 党组织名称
     * @param orgType 组织类型
     * @param secretaryName 书记姓名
     * @return 党建组织信息列表
     */
    @Override
    public IPage<VillagePartySimpleVO> getList(Long current, Long size, String orgName, String orgType, String secretaryName) {
        LambdaQueryWrapper<VillagePartyEntity> queryWrapper = new LambdaQueryWrapper<VillagePartyEntity>()
        .like(StringUtils.hasText(orgName), VillagePartyEntity::getOrgName, orgName)
        .like(StringUtils.hasText(orgType), VillagePartyEntity::getOrgType, orgType)
        .like(StringUtils.hasText(secretaryName), VillagePartyEntity::getSecretaryName, secretaryName)
        .orderByDesc(VillagePartyEntity::getCreateTime);
        IPage<VillagePartyEntity> entityPage = page(new Page<>(current, size), queryWrapper);
        return entityPage.convert(
            entity -> {
                VillagePartySimpleVO vo = new VillagePartySimpleVO();
                BeanUtils.copyProperties(entity, vo);
                return vo;
            }
        );
    }
}

