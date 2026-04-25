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
}

