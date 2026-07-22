package com.backend.common.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.backend.common.filter.JwtSecurityFilter;
import com.backend.common.filter.TraceIdFilter;

import lombok.RequiredArgsConstructor;

/**
 * @author chengyang &#064;date 2026/4/16 &#064;description SecurityFilterChain
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  @Value(
      "${app.cors.allowed-origin-patterns:http://localhost,http://localhost:*,http://127.0.0.1,http://127.0.0.1:*,http://192.168.*,http://192.168.*:*,http://10.*,http://10.*:*,http://172.16.*,http://172.16.*:*,http://172.17.*,http://172.17.*:*,http://172.18.*,http://172.18.*:*,http://172.19.*,http://172.19.*:*,http://172.2*.*,http://172.2*.*:*,http://172.30.*,http://172.30.*:*,http://172.31.*,http://172.31.*:*}")
  private String allowedOriginPatterns;

  private final TraceIdFilter traceIdFilter;
  private final JwtSecurityFilter jwtSecurityFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF保护
        .sessionManagement(
            sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 禁用会话管理
        .authorizeHttpRequests(
            auth ->
                auth // 授权请求
                    .requestMatchers(
                        "/auth/login",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/auth/usernames",
                        "/features/**",
                        "/announcements/**",
                        "/guest/**",
                        "/village-affairs/**",
                        "/public/village-affairs/**",
                        "/internal/auth/**"
                      )
                    .permitAll() // 白名单请求放行（前台村务公示为 /public/village-affairs）
                    // 当前登录信息：只要已登录即可查询（管理员/村干部/村民都可）
                    .requestMatchers("/admin/me", "/media/page", "/media/upload", "/media/{id}")
                    .authenticated()
                    // 留言：公开列表可读；创建/我的留言需登录
                    .requestMatchers(HttpMethod.GET, "/interactions/messages")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/interactions/messages")
                    .hasAuthority("ROLE_VILLAGER")
                    .requestMatchers("/interactions/messages/my/**")
                    .hasAuthority("ROLE_VILLAGER")
                    // 村民民生服务工单
                    .requestMatchers("/villager/management/services/**")
                    .hasAuthority("ROLE_VILLAGER")
                    // 管理员仅负责账号管理
                    .requestMatchers("/admin/users/**")
                    .hasAuthority("ROLE_ADMIN")
                    // 村干部负责业务处理（公告、留言）
                    .requestMatchers(
                        "/cadre/announcements/**",
                        "/cadre/management/services/**",
                        "/cadre/management/population/**",
                        "/cadre/interactions/**",
                        "/cadre/features/**",
                        "/cadre/village-population/**",
                        "/cadre/village-house-land/**",
                        "/cadre/village-party/**",
                        "/cadre/village-affairs/**",
                        "/media/cadre/**")
                    .hasAuthority("ROLE_CADRE")
                    .requestMatchers("/villager/**")
                    .hasAnyAuthority("ROLE_VILLAGER") // 村民请求需要认证
                    .anyRequest()
                    .authenticated() // 任何请求都需要认证
            )
        // 须先把 JwtSecurityFilter 挂到链上，才能用其 class 作为 TraceIdFilter 的定位锚点
        .addFilterBefore(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(traceIdFilter, JwtSecurityFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // Deployment verification first: open CORS temporarily to isolate browser-origin issues.
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    configuration.setExposedHeaders(List.of("token", "X-Trace-Id"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private List<String> parseAllowedOriginPatterns() {
    return List.of(allowedOriginPatterns.split(","))
        .stream()
        .map(String::trim)
        .filter(pattern -> !pattern.isEmpty())
        .collect(Collectors.toList());
  }
}
