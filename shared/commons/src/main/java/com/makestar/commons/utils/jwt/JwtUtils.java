package com.makestar.commons.utils.jwt;

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

/**
 * JWT(JSON Web Token) 생성, 파싱, 검증을 처리하는 유틸리티 클래스입니다.
 * 액세스 토큰과 리프레시 토큰의 생성 및 관리를 담당합니다.
 */
@Slf4j
public class JwtUtils {

    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final Key key;
    private final JwtParser jwtParser;

    /**
     * JwtUtils 클래스의 생성자입니다.
     * 
     * @param secret JWT 서명에 사용될 비밀키
     * @param accessTokenExpiration 액세스 토큰의 만료 시간 (밀리초)
     * @param refreshTokenExpiration 리프레시 토큰의 만료 시간 (밀리초)
     */
    public JwtUtils(String secret, long accessTokenExpiration, long refreshTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    /**
     * 사용자 인증을 위한 액세스 토큰을 생성합니다.
     * 
     * @param subject 토큰의 주체 (일반적으로 사용자 ID나 이메일)
     * @param claims 토큰에 포함될 추가 정보
     * @return 생성된 JWT 액세스 토큰
     */
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, accessTokenExpiration);
    }

    /**
     * 액세스 토큰 재발급을 위한 리프레시 토큰을 생성합니다.
     * 
     * @param subject 토큰의 주체 (일반적으로 사용자 ID나 이메일)
     * @return 생성된 JWT 리프레시 토큰
     */
    public String generateRefreshToken(String subject) {
        return generateToken(subject, null, refreshTokenExpiration);
    }

    /**
     * JWT 토큰을 생성하는 내부 메소드입니다.
     * 
     * @param subject 토큰의 주체
     * @param claims 토큰에 포함될 추가 정보 (선택적)
     * @param expiration 토큰의 만료 시간 (밀리초)
     * @return 생성된 JWT 토큰
     */
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

    /**
     * 토큰에서 사용자명(subject)을 추출합니다.
     * 
     * @param token JWT 토큰
     * @return 토큰에서 추출한 사용자명
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 토큰의 만료 일자를 추출합니다.
     * 
     * @param token JWT 토큰
     * @return 토큰의 만료 일자
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 특정 클레임을 추출합니다.
     * 
     * @param token JWT 토큰
     * @param claimsResolver 클레임을 처리할 함수
     * @return 처리된 클레임 값
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 클레임을 추출합니다.
     * 
     * @param token JWT 토큰
     * @return 토큰의 모든 클레임
     */
    private Claims extractAllClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    /**
     * 토큰이 만료되었는지 확인합니다.
     * 
     * @param token JWT 토큰
     * @return 토큰 만료 여부 (만료된 경우 true)
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.error("토큰 만료 검증 중 에러 발생: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 토큰의 유효성을 검증합니다.
     * 토큰의 사용자명이 일치하고, 만료되지 않았는지 확인합니다.
     * 
     * @param token JWT 토큰
     * @param username 검증할 사용자명
     * @return 토큰 유효성 여부
     */
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