package com.backend.management.service;

import com.backend.management.dto.VillagePartyCreateDTO;
import com.backend.management.dto.VillagePartyUpdateDTO;
import com.backend.management.entity.VillagePartyEntity;
import com.backend.management.vo.VillagePartyDetailVO;
import com.backend.management.vo.VillagePartySimpleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
/**
 *
 */
public interface VillagePartyService extends IService<VillagePartyEntity> {
    
    /**
     * 创建党组织
     * @param dto 党组织创建DTO
     * @return 党组织ID
     */
    Integer create(VillagePartyCreateDTO dto);

    /**
     * 分页查询党建组织信息列表
     * @param current 当前页
     * @param size 每页条数
     * @return 党建组织信息列表
     */
    IPage<VillagePartySimpleVO> getList(Long current, Long size, String orgName, String orgType, String secretaryName);
}

