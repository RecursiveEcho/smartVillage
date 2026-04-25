package com.backend.management.service;

import com.backend.management.dto.VillagePopulationCreateDTO;
import com.backend.management.dto.VillagePopulationUpdateDTO;
import com.backend.management.entity.VillagePopulationEntity;
import com.backend.management.vo.VillagePopulationDetailVO;
import com.backend.management.vo.VillagePopulationSimpleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

public interface VillagePopulationService extends IService<VillagePopulationEntity> {

    /**
     * 创建人口台账
     * @param villagePopulationCreateDTO 人口台账创建DTO
     */
    void createVillagePopulation(VillagePopulationCreateDTO villagePopulationCreateDTO);
    
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
    IPage<VillagePopulationSimpleVO> getVillagePopulationList(Long current, Long size, String householdNo, String fullName, Integer gender, String relationToHead);

    /**
     * 根据id获取人口台账详情
     * @param id 人口台账id
     * @return 人口台账详情
     */
    VillagePopulationDetailVO getVillagePopulationDetail(Long id);
}

