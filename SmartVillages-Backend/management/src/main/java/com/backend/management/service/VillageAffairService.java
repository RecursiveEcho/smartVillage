package com.backend.management.service;

import com.backend.management.dto.VillageAffairAuditDTO;
import com.backend.management.dto.VillageAffairCreateDTO;
import com.backend.management.dto.VillageAffairUpdateDTO;
import com.backend.management.entity.VillageAffairEntity;
import com.backend.management.vo.VillageAffairDetailVO;
import com.backend.management.vo.VillageAffairSimpleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface VillageAffairService extends IService<VillageAffairEntity> {
    /**
     * 创建村务事项/公示
     * @param dto 村务事项/公示创建DTO
     * @return 村务事项/公示ID
     */
    Integer create(VillageAffairCreateDTO dto);
}

