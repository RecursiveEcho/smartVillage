package com.backend.common.utils;

import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
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
    @Value("${jwt.secret:smartVillages}")
    private  String secretKey;
    @Value("${jwt.expiration-ms:86400000}")
    private  long expirationMs;

    public String generateToken(String Id,String username,String role) {
        return Jwts.builder()
                .setSubject(Id + ":" + username + ":" + role)
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public String parseToken(String token) throws Exception {
        if (token == null || token.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        String subject = Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        if(!StringUtils.hasText(subject)){
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        return subject;
    }   
}
