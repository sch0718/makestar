package com.makestar.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class JwtUtils {

    private final String secret;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final Key key;
    private final JwtParser jwtParser;

    public JwtUtils(String secret, long accessTokenExpiration, long refreshTokenExpiration) {
        this.secret = secret;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    // 액세스 토큰 생성
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, accessTokenExpiration);
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(String subject) {
        return generateToken(subject, null, refreshTokenExpiration);
    }

    // 토큰 생성 메소드
    private String generateToken(String subject, Map<String, Object> claims, long expiration) {
        long now = System.currentTimeMillis();
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(key);
        
        if (claims != null) {
            jwtBuilder.addClaims(claims);
        }
        
        return jwtBuilder.compact();
    }

    // 토큰에서 사용자명 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 토큰 만료일자 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 클레임 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 모든 클레임 추출
    private Claims extractAllClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.error("토큰 만료 검증 중 에러 발생: {}", e.getMessage());
            return true;
        }
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("토큰 검증 중 에러 발생: {}", e.getMessage());
            return false;
        }
    }
} 