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

}

