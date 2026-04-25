package com.backend.management.controller;

import com.backend.common.result.Result;
import com.backend.management.dto.VillageAffairAuditDTO;
import com.backend.management.dto.VillageAffairCreateDTO;
import com.backend.management.dto.VillageAffairUpdateDTO;
import com.backend.management.service.VillageAffairService;
import com.backend.management.vo.VillageAffairDetailVO;
import com.backend.management.vo.VillageAffairSimpleVO;
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

@RestController
@RequiredArgsConstructor
@Tag(name = "村务管理-村务事项/公示", description = "村务事项/公示接口")
public class VillageAffairController {

    private final VillageAffairService villageAffairService;

    // ---------------- 管理端（村干部） ----------------

    /**
     * 创建村务事项/公示
     * @param dto 村务事项/公示创建DTO
     * @return 村务事项/公示ID
     */
    @Operation(summary = "创建村务事项/公示")
    @PostMapping("/cadre/village-affairs")
    public Result<Integer> create(@RequestBody VillageAffairCreateDTO dto) {
        return Result.success(villageAffairService.create(dto));
    }

    /**
     * 分页查询村务事项/公示列表
     * @param current 当前页
     * @param size 每页条数
     * @return 村务事项/公示列表
     */
    @Operation(summary = "分页查询村务事项/公示列表")
    @GetMapping("/cadre/village-affairs")
    public Result<IPage<VillageAffairSimpleVO>> getList(
        @RequestParam(defaultValue = "1") Long current, 
        @RequestParam(defaultValue = "10") Long size,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) String affairType,
        @RequestParam(required = false) String title
    ) {
        return Result.success(villageAffairService.getList(current, size, status, affairType, title));
    }

    /**
     * 根据id获取村务事项/公示详情
     * @param id 村务事项/公示id
     * @return 村务事项/公示详情
     */
    @Operation(summary = "根据id获取村务事项/公示详情")
    @GetMapping("/cadre/village-affairs/{id}")
    public Result<VillageAffairDetailVO> getDetail(@PathVariable Integer id) {
        return Result.success(villageAffairService.getDetail(id));
    }

    /**
     * 更新村务事项/公示
     * @param id 村务事项/公示id
     * @param dto 村务事项/公示更新DTO
     * @return 操作结果文案
     */
    @Operation(summary = "更新村务事项/公示")
    @PutMapping("/cadre/village-affairs/{id}")
    public Result<String> update(@PathVariable Integer id, @RequestBody VillageAffairUpdateDTO dto) {
        villageAffairService.update(id, dto);
        return Result.success("村务事项/公示更新成功");
    }

    /**
     * 删除村务事项/公示
     * @param id 村务事项/公示id
     * @return 操作结果文案
     */
    @Operation(summary = "删除村务事项/公示")
    @DeleteMapping("/cadre/village-affairs/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        villageAffairService.delete(id);
        return Result.success("村务事项/公示删除成功");
    }

    /**
     * 审核村务事项/公示
     * @param id 村务事项/公示id
     * @param dto 村务事项/公示审核DTO
     * @return 操作结果文案
     */
    @Operation(summary = "审核村务事项/公示")
    @PostMapping("/cadre/village-affairs/{id}/audit")
    public Result<String> audit(@PathVariable Integer id, @RequestBody VillageAffairAuditDTO dto) {
        villageAffairService.audit(id, dto);
        return Result.success("村务事项/公示审核成功");
    }

    @Operation(summary = "前台分页查询村务事项/公示列表")
    @GetMapping("/public/village-affairs")
    public Result<IPage<VillageAffairSimpleVO>> getPublicList(
        @RequestParam(defaultValue = "1") Long current, 
        @RequestParam(defaultValue = "10") Long size,
        @RequestParam(required = false) String affairType,
        @RequestParam(required = false) String title
    ) {
        return Result.success(villageAffairService.getPublicList(current, size, affairType, title));
    }
}

