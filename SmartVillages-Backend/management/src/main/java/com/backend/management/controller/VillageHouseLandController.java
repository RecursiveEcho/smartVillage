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
    
}

