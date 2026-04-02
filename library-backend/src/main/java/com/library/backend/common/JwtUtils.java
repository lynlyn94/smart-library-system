package com.library.backend.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    // 秘钥（相当于印钞机的模具，不能泄露）
    private static final String SECRET = "library_system_secret_key_2026";
    // 过期时间：7天 (单位：毫秒)
    private static final long EXPIRE = 604800000L;

    /**
     * 生成 Token
     */
    public static String createToken(String userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }
}