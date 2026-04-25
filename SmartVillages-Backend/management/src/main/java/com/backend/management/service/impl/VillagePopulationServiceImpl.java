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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Objects;

@Service
public class VillagePopulationServiceImpl
        extends ServiceImpl<VillagePopulationMapper, VillagePopulationEntity>
        implements VillagePopulationService {

    /**
     * 创建人口台账
     * @param villagePopulationCreateDTO 人口台账创建DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createVillagePopulation(VillagePopulationCreateDTO villagePopulationCreateDTO) {
        VillagePopulationEntity villagePopulationEntity = new VillagePopulationEntity();
        BeanUtils.copyProperties(villagePopulationCreateDTO, villagePopulationEntity);
        save(villagePopulationEntity);
    }

    /**
     * 分页查询人口台账列表
     * @param current 当前页
     * @param size 每页条数
     * @param householdNo 户号
     * @param fullName 姓名
     * @param gender 性别
     * @param relationToHead 与户主关系
     * @return 分页查询结果
     */
    @Override
    public IPage<VillagePopulationSimpleVO> getVillagePopulationList(Long current, Long size, String householdNo, String fullName, Integer gender, String relationToHead) {
        LambdaQueryWrapper<VillagePopulationEntity> queryWrapper = new LambdaQueryWrapper<VillagePopulationEntity>()
        .like(StringUtils.hasText(householdNo), VillagePopulationEntity::getHouseholdNo, householdNo)
        .like(StringUtils.hasText(fullName), VillagePopulationEntity::getFullName, fullName)
        .eq(gender != null, VillagePopulationEntity::getGender, gender)
        .eq(StringUtils.hasText(relationToHead), VillagePopulationEntity::getRelationToHead, relationToHead)
        .orderByDesc(VillagePopulationEntity::getCreateTime);
        IPage<VillagePopulationEntity> entityPage = page(new Page<>(current, size), queryWrapper);
        return entityPage.convert(
                entity -> {
                    VillagePopulationSimpleVO vo = new VillagePopulationSimpleVO();
                    BeanUtils.copyProperties(Objects.requireNonNull(entity), vo);
                    return vo;
                }
        );
    }

    /**
     * 根据id获取人口台账详情
     * @param id 人口台账id
     * @return 人口台账详情
     */
    @Override
    public VillagePopulationDetailVO getVillagePopulationDetail(Long id) {
        VillagePopulationEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "人口台账不存在");
        }
        VillagePopulationDetailVO vo = new VillagePopulationDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 更新人口台账
     * @param id 人口台账id
     * @param villagePopulationUpdateDTO 人口台账更新DTO
     */
    @Override
    public void updateVillagePopulation(Long id, VillagePopulationUpdateDTO villagePopulationUpdateDTO) {
        VillagePopulationEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "人口台账不存在");
        }
        BeanUtils.copyProperties(villagePopulationUpdateDTO, entity);
        updateById(entity);
    }
}

