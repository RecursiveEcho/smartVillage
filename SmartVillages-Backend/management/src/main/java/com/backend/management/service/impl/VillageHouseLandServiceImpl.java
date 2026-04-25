package com.backend.management.service.impl;

import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.management.dto.VillageHouseLandCreateDTO;
import com.backend.management.dto.VillageHouseLandUpdateDTO;
import com.backend.management.entity.VillageHouseLandEntity;
import com.backend.management.mapper.VillageHouseLandMapper;
import com.backend.management.service.VillageHouseLandService;
import com.backend.management.vo.VillageHouseLandDetailVO;
import com.backend.management.vo.VillageHouseLandSimpleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import com.backend.common.utils.RedisJsonCacheTool;
import com.backend.common.utils.CacheKeyUtils;

@Service
@RequiredArgsConstructor
public class VillageHouseLandServiceImpl
        extends ServiceImpl<VillageHouseLandMapper, VillageHouseLandEntity>
        implements VillageHouseLandService {
        
    private static final String CACHE_KEY_PREFIX = "village_house_land:";
    private final RedisJsonCacheTool redisJsonCacheTool;
    /**
     * 创建房屋与土地台账
     * @param villageHouseLandCreateDTO 房屋与土地台账创建DTO
     * @return 房屋与土地台账id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createVillageHouseLand(VillageHouseLandCreateDTO villageHouseLandCreateDTO) {
        VillageHouseLandEntity villageHouseLandEntity = new VillageHouseLandEntity();
        BeanUtils.copyProperties(villageHouseLandCreateDTO, villageHouseLandEntity);
        save(villageHouseLandEntity);
        Integer id = villageHouseLandEntity.getId();
        redisJsonCacheTool.setObject(CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id), villageHouseLandEntity);
        return id;
    }

    /**
     * 分页查询房屋与土地台账列表
     * @param current 当前页
     * @param size 每页条数
     * @param bizType 类型
     * @param ownerName 权利人/户主
     * @param location 坐落
     * @return 房屋与土地台账列表
     */
    @Override
    public IPage<VillageHouseLandSimpleVO> getVillageHouseLandList(Long current, Long size, String bizType, String ownerName, String location) {
        LambdaQueryWrapper<VillageHouseLandEntity> queryWrapper = new LambdaQueryWrapper<VillageHouseLandEntity>()
        .eq(StringUtils.hasText(bizType), VillageHouseLandEntity::getBizType, bizType)
        .like(StringUtils.hasText(ownerName), VillageHouseLandEntity::getOwnerName, ownerName)
        .like(StringUtils.hasText(location), VillageHouseLandEntity::getLocation, location)
        .orderByDesc(VillageHouseLandEntity::getCreateTime);
        IPage<VillageHouseLandEntity> entityPage = page(new Page<>(current, size), queryWrapper);
        return entityPage.convert(entity -> {
            VillageHouseLandSimpleVO vo = new VillageHouseLandSimpleVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }

    /**
     * 根据id获取房屋与土地台账详情
     * @param id 房屋与土地台账id
     * @return 房屋与土地台账详情
     */
    @Override
    public VillageHouseLandDetailVO getVillageHouseLandDetail(Integer id) {
        String cacheKey = CacheKeyUtils.detailKey(CACHE_KEY_PREFIX, id);
        VillageHouseLandDetailVO fromCache = redisJsonCacheTool.getObject(cacheKey, VillageHouseLandDetailVO.class);
        if (fromCache != null) {
            return fromCache;
        }
        VillageHouseLandEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "房屋与土地台账不存在");
        }
        VillageHouseLandDetailVO vo = new VillageHouseLandDetailVO();
        BeanUtils.copyProperties(entity, vo);
        redisJsonCacheTool.setObject(cacheKey, vo);
        return vo;
    }
}
