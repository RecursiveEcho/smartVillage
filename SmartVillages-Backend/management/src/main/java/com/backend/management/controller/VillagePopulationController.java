package com.backend.management.controller;

import com.backend.common.result.Result;
import com.backend.management.dto.VillagePopulationCreateDTO;
import com.backend.management.dto.VillagePopulationUpdateDTO;
import com.backend.management.service.VillagePopulationService;
import com.backend.management.vo.VillagePopulationDetailVO;
import com.backend.management.vo.VillagePopulationSimpleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.time.LocalDate;
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
@Tag(name = "村务管理-人口台账", description = "人口台账接口")
public class VillagePopulationController {

    private final VillagePopulationService villagePopulationService;

    /**
     * 创建人口台账
     * @param villagePopulationCreateDTO 人口台账创建DTO
     * @return 操作结果文案
     */
    @Operation(summary = "创建人口台账")
    @PostMapping("/cadre/village-population")
    public Result<String> createVillagePopulation(@RequestBody @Valid VillagePopulationCreateDTO villagePopulationCreateDTO) {
        villagePopulationService.createVillagePopulation(villagePopulationCreateDTO);
        return Result.success("人口台账创建成功");
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
    @Operation(summary = "分页查询人口台账列表")
    @GetMapping("/cadre/village-population")
    public Result<IPage<VillagePopulationSimpleVO>> getVillagePopulationList(
        @RequestParam(defaultValue = "1") Long current, 
        @RequestParam(defaultValue = "10") Long size,
        @RequestParam(required = false) String householdNo,
        @RequestParam(required = false) String fullName,
        @RequestParam(required = false) Integer gender,
        @RequestParam(required = false) String relationToHead
        ) {
        return Result.success(villagePopulationService.getVillagePopulationList(current, size, householdNo, fullName, gender, relationToHead));
    }

    /**
     * 根据id获取人口台账详情
     * @param id 人口台账id
     * @return 人口台账详情
     */
    @Operation(summary = "根据id获取人口台账详情")
    @GetMapping("/cadre/village-population/{id}")
    public Result<VillagePopulationDetailVO> getVillagePopulationDetail(@PathVariable Long id) {
        return Result.success(villagePopulationService.getVillagePopulationDetail(id));
    }

    /**
     * 更新人口台账
     * @param id 人口台账id
     * @param villagePopulationUpdateDTO 人口台账更新DTO
     * @return 操作结果文案
     */
    @Operation(summary = "更新人口台账")
    @PutMapping("/cadre/village-population/{id}")
    public Result<String> updateVillagePopulation(@PathVariable Long id, @RequestBody @Valid VillagePopulationUpdateDTO villagePopulationUpdateDTO) {
        villagePopulationService.updateVillagePopulation(id, villagePopulationUpdateDTO);
        return Result.success("人口台账更新成功");
    }

    /**
     * 删除人口台账
     * @param id 人口台账id
     * @return 操作结果文案
     */
    @Operation(summary = "删除人口台账")
    @DeleteMapping("/cadre/village-population/{id}")
    public Result<String> deleteVillagePopulation(@PathVariable Long id) {
        villagePopulationService.deleteVillagePopulation(id);
        return Result.success("人口台账删除成功");
    }
}

