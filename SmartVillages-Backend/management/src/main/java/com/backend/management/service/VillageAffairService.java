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

    /**
     * 分页查询村务事项/公示列表
     * @param current 当前页
     * @param size 每页条数
     * @return 村务事项/公示列表
     */
    IPage<VillageAffairSimpleVO> getList(Long current, Long size,Integer status, String affairType, String title);

    /**
     * 根据id获取村务事项/公示详情
     * @param id 村务事项/公示id
     * @return 村务事项/公示详情
     */
    VillageAffairDetailVO getDetail(Integer id);

    /**
     * 更新村务事项/公示
     * @param id 村务事项/公示id
     * @param dto 村务事项/公示更新DTO
     */
    void update(Integer id, VillageAffairUpdateDTO dto);

    /**
     * 删除村务事项/公示
     * @param id 村务事项/公示id
     */
    void delete(Integer id);

    /**
     * 审核村务事项/公示
     * @param id 村务事项/公示id
     * @param dto 村务事项/公示审核DTO
     */
    void audit(Integer id, VillageAffairAuditDTO dto);

    /*
     * 前台分页查询村务事项/公示列表
     * @param current 当前页
     * @param size 每页条数
     * @return 村务事项/公示列表
     */
    IPage<VillageAffairSimpleVO> getPublicList(Long current, Long size, String affairType, String title);
}

