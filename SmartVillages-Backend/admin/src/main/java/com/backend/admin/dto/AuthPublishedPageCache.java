package com.backend.admin.dto;

import java.util.List;

import com.backend.auth.vo.AuthVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "认证用户分页缓存")
public class AuthPublishedPageCache {
    @Schema(description = "当前页数据")
    private List<AuthVO> records;

    @Schema(description = "总条数")
    private long total;

    @Schema(description = "当前页码")
    private long current;

    @Schema(description = "每页条数")
    private long size;

    @Schema(description = "总页数")
    private long pages;
}
