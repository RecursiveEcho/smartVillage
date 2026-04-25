package com.backend.management.controller;

import com.backend.common.result.Result;
import com.backend.management.dto.VillageHouseLandCreateDTO;
import com.backend.management.dto.VillageHouseLandUpdateDTO;
import com.backend.management.service.VillageHouseLandService;
import com.backend.management.vo.VillageHouseLandDetailVO;
import com.backend.management.vo.VillageHouseLandSimpleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "村务管理-房屋与土地台账", description = "房屋与土地台账接口")
public class VillageHouseLandController {

    private final VillageHouseLandService villageHouseLandService;

    /**
     * 创建房屋与土地台账
     * @param villageHouseLandCreateDTO 房屋与土地台账创建DTO
     * @return 操作结果文案
     */
    @Operation(summary = "创建房屋与土地台账")
    @PostMapping("/cadre/village-house-land")
    public Result<Integer> createVillageHouseLand(@RequestBody VillageHouseLandCreateDTO villageHouseLandCreateDTO) {
        Integer id = villageHouseLandService.createVillageHouseLand(villageHouseLandCreateDTO);
        return Result.success(id);
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
    @Operation(summary = "分页查询房屋与土地台账列表")
    @GetMapping("/cadre/village-house-land")
    public Result<IPage<VillageHouseLandSimpleVO>> getVillageHouseLandList(
        @RequestParam(defaultValue = "1") Long current,
        @RequestParam(defaultValue = "10") Long size,
        @RequestParam(required = false) String bizType,
        @RequestParam(required = false) String ownerName,
        @RequestParam(required = false) String location) {
        return Result.success(villageHouseLandService.getVillageHouseLandList(current, size, bizType, ownerName, location));
    }

    /**
     * 根据id获取房屋与土地台账详情
     * @param id 房屋与土地台账id
     * @return 房屋与土地台账详情
     */
    @Operation(summary = "根据id获取房屋与土地台账详情")
    @GetMapping("/cadre/village-house-land/{id}")
    public Result<VillageHouseLandDetailVO> getVillageHouseLandDetail(@PathVariable Integer id) {
        return Result.success(villageHouseLandService.getVillageHouseLandDetail(id));
    }

    /**
     * 更新房屋与土地台账
     * @param id 房屋与土地台账id
     * @param villageHouseLandUpdateDTO 房屋与土地台账更新DTO
     * @return 操作结果文案
     */
    @Operation(summary = "更新房屋与土地台账")
    @PutMapping("/cadre/village-house-land/{id}")
    public Result<String> updateVillageHouseLand(@PathVariable Integer id, @RequestBody VillageHouseLandUpdateDTO villageHouseLandUpdateDTO) {
        villageHouseLandService.update(id, villageHouseLandUpdateDTO);
        return Result.success("更新成功");
    }
}

