package com.backend.common.config;

import java.util.Arrays;
import java.util.List;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class IsWhitelistConfig {
    /** 白名单 */
    private static final List<String> WHITELIST = Arrays.asList(
        "/auth/login",
        "/v3/api-docs",
        "/doc.html",
        "/swagger-ui/**",
        "/webjars/**"
    );
/**
 * 获取白名单
 * @return 白名单
 */
    public static List<String> getWhitelist() {
        return WHITELIST;
    }
}
