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

    /**
     * 分页查询房屋与土地台账列表
     * @param current 当前页
     * @param size 每页条数
     * @param bizType 类型
     * @param ownerName 权利人/户主
     * @param location 坐落
     * @return 房屋与土地台账列表
     */
   IPage<VillageHouseLandSimpleVO> getVillageHouseLandList(Long current, Long size, String bizType, String ownerName, String location);

    /**
     * 根据id获取房屋与土地台账详情
     * @param id 房屋与土地台账id
     * @return 房屋与土地台账详情
     */
    VillageHouseLandDetailVO getVillageHouseLandDetail(Integer id);
    
}

