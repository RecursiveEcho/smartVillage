package com.backend.management.service;

import com.backend.management.dto.VillageHouseLandCreateDTO;
import com.backend.management.dto.VillageHouseLandUpdateDTO;
import com.backend.management.entity.VillageHouseLandEntity;
import com.backend.management.vo.VillageHouseLandDetailVO;
import com.backend.management.vo.VillageHouseLandSimpleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

public interface VillageHouseLandService extends IService<VillageHouseLandEntity> {

    /**
     * 创建房屋与土地台账
     * @param villageHouseLandCreateDTO 房屋与土地台账创建DTO
     */
    Integer createVillageHouseLand(VillageHouseLandCreateDTO villageHouseLandCreateDTO);
}

