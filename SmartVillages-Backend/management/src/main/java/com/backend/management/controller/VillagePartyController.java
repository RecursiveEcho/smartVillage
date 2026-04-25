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

@RestController
@RequiredArgsConstructor
@Tag(name = "村务管理-党建组织信息", description = "党建组织信息接口")
public class VillagePartyController {

    private final VillagePartyService villagePartyService;

    
}

