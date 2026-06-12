package com.example.demo.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKeyForJwtTokenGenerationAndValidationWithMinimumLength}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}")
    private long expirationTime;

    // JWT 토큰 생성
    public String generateToken(String memberId, String email, String name) {

        try {
            System.out.println("secretKey = " + secretKey);
            System.out.println("secretKey length = " + secretKey.length());

            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

            System.out.println("B");

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationTime);

            System.out.println("C");

            String token = Jwts.builder()
                    .setSubject(memberId)
                    .claim("email", email)
                    .claim("name", name)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();

            System.out.println("D");

            return token;

        } catch (Exception e) {
            System.out.println("JWT 생성 실패");
            e.printStackTrace();
            throw e;
        }
    }

    // 토큰에서 memberId 추출
    public String getMemberIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
