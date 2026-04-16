package com.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.backend.common.filter.JwtSecurityFilter;
/**
 * @author chengyang
 * &#064;date  2026/4/16
 * &#064;description  SecurityFilterChain
 */

@Configuration
public class SecurityConfig {
    private final JwtSecurityFilter jwtSecurityFilter;
    public SecurityConfig(JwtSecurityFilter jwtSecurityFilter) {
        this.jwtSecurityFilter = jwtSecurityFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/auth/login",
                "/v3/api-docs/**",
                "/doc.html",
                "/swagger-ui/**",
                "/webjars/**",
                "/swagger-resources/**"
            ).permitAll()
            .anyRequest().authenticated()
        )
            .addFilterBefore(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
