package com.backend.management.controller;

import com.backend.common.result.Result;
import com.backend.management.dto.VillagePartyCreateDTO;
import com.backend.management.dto.VillagePartyUpdateDTO;
import com.backend.management.service.VillagePartyService;
import com.backend.management.vo.VillagePartyDetailVO;
import com.backend.management.vo.VillagePartySimpleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * @author 村务管理-党建组织信息
 * &#064;date 2026/4/25
 * &#064;description 党建组织信息控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "村务管理-党建组织信息", description = "党建组织信息接口")
public class VillagePartyController {

    private final VillagePartyService villagePartyService;

    /**
     * 创建党建组织信息
     * @param dto 党建组织信息创建DTO
     * @return 党组织ID
     */
    @Operation(summary = "创建党建组织信息")
    @PostMapping("/cadre/village-party")
    public Result<Integer> create(@RequestBody VillagePartyCreateDTO dto) {
        Integer id = villagePartyService.create(dto);
        return Result.success(id);
    }
    /**
     * 分页查询党建组织信息列表
     * @param current 当前页
     * @param size 每页条数
     * @return 党建组织信息列表
     */
    @Operation(summary = "分页查询党建组织信息列表")
    @GetMapping("/cadre/village-party")
    public Result<IPage<VillagePartySimpleVO>> getList(
        @RequestParam(defaultValue = "1") Long current, 
        @RequestParam(defaultValue = "10") Long size,
        @RequestParam(required = false) String orgName,
        @RequestParam(required = false) String orgType,
        @RequestParam(required = false) String secretaryName
    ) {
        return Result.success(villagePartyService.getList(current, size, orgName, orgType, secretaryName));
    }

    /**
     * 根据id获取党建组织信息详情
     * @param id 党建组织信息id
     * @return 党建组织信息详情
     */
    @Operation(summary = "根据id获取党建组织信息详情")
    @GetMapping("/cadre/village-party/{id}")
    public Result<VillagePartyDetailVO> getDetail(@PathVariable Integer id) {
        return Result.success(villagePartyService.getDetail(id));
    }

    /**
     * 更新党建组织信息
     * @param id 党建组织信息id
     * @param dto 党建组织信息更新DTO
     * @return 操作结果文案
     */
    @Operation(summary = "更新党建组织信息")
    @PutMapping("/cadre/village-party/{id}")
    public Result<String> update(@PathVariable Integer id, @RequestBody VillagePartyUpdateDTO dto) {
        villagePartyService.update(id, dto);
        return Result.success("党建组织信息更新成功");
    }

    /**
     * 删除党建组织信息
     * @param id 党建组织信息id
     * @return 操作结果文案
     */
    @Operation(summary = "删除党建组织信息")
    @DeleteMapping("/cadre/village-party/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        villagePartyService.delete(id);
        return Result.success("党建组织信息删除成功");
    }
}

