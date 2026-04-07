package com.backend.common.config;

import com.backend.common.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author chenyang
 * @date 2026/4/2
 * @description 拦截器配置类
 */
@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 统一对业务接口做 token 校验；具体白名单由 AuthInterceptor 自己处理。
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
    }
}