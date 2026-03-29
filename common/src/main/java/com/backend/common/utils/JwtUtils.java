package com.backend.common.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author chenyang
 * @date 2026/3/27
 * @description JWT 工具类（jjwt 0.9.x：签发与解析须使用同一 secret）
 */
@Component
public class JwtUtils {

    private static final String secretKey = "smartVillages";
    private static final long expiration = 1000L * 60 * 60 * 24 * 7; // 7 天

    public static String generateToken(String userId, String username) {
        return Jwts.builder()
                .setSubject(userId + ":" + username)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public static String parseToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

            return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }
}
