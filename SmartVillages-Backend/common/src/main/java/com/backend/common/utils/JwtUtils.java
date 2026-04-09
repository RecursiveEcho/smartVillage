package com.backend.common.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author chenyang
 * @date 2026/4/2
 * @description JWT 工具类
 */
@Component
public class JwtUtils {

    // 为了最大化与现有静态调用兼容：静态入口方法使用静态字段。
    private static String SECRET_KEY;
    private static long EXPIRATION_MS;

    // 从 application.yml 读取配置
    @Value("${jwt.secret:smartVillages}")
    private String secretKey;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    @PostConstruct
    private void init() {
        SECRET_KEY = secretKey;
        EXPIRATION_MS = expirationMs;
    }

    public static String generateToken(String userId,String username,String role) {
        return Jwts.builder()
                .setSubject(userId + ":" + username + ":" + role)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public static String parseToken(String token) throws Exception {
        if (token == null || token.isBlank()) {
            throw new Exception("token is blank");
        }
        String subject = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        if(!StringUtils.hasText(subject)){
            throw new Exception("token is invalid");
        }
        return subject;
    }   
}
