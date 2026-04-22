package com.backend.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.backend.common.filter.JwtSecurityFilter;
/**
 * @author chengyang
 * &#064;date  2026/4/16
 * &#064;description  SecurityFilterChain
 */

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtSecurityFilter jwtSecurityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)//禁用CSRF保护
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//禁用会话管理
            .authorizeHttpRequests(auth -> auth//授权请求
            .requestMatchers(
                "/auth/login",
                "/v3/api-docs/**",
                "/doc.html",
                "/swagger-ui/**",
                "/webjars/**",
                "/swagger-resources/**",
                "/announcements/**",
                "/guest/**"
            ).permitAll()//白名单请求放行
            // 当前登录信息：只要已登录即可查询（管理员/村干部/村民都可）
            .requestMatchers("/admin/me").authenticated()
            // 留言：公开列表可读；创建/我的留言需登录
            .requestMatchers(HttpMethod.GET, "/interactions/messages").permitAll()
            
            .requestMatchers(HttpMethod.POST, "/interactions/messages").hasAuthority("ROLE_VILLAGER")
            .requestMatchers("/interactions/messages/my/**").hasAuthority("ROLE_VILLAGER")
            // 管理员仅负责账号管理
            .requestMatchers("/admin/users/**", "/media/images/upload").hasAuthority("ROLE_ADMIN")
            // 村干部负责业务处理（公告、留言）
            .requestMatchers("/cadre/announcements/**", "/cadre/interactions/**", "/media/images/upload").hasAuthority("ROLE_CADRE")
            .requestMatchers("/villager/**").hasAnyAuthority("ROLE_VILLAGER")//村民请求需要认证
            .anyRequest().authenticated()//任何请求都需要认证
        )
        //在UsernamePasswordAuthenticationFilter之前添加JwtSecurityFilter
            .addFilterBefore(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
