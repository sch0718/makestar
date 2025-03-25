package com.makestar.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JWT(JSON Web Token) 설정 클래스
 * 
 * <p>API Gateway에서 사용되는 JWT 관련 설정과 유틸리티를 정의합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>JWT 토큰 검증 로직</li>
 *   <li>토큰 서명 키 설정</li>
 *   <li>토큰 만료 시간 설정</li>
 *   <li>토큰 파싱 및 클레임 추출</li>
 *   <li>인증/인가 필터 연동</li>
 * </ul>
 * 
 * <p>보안 고려사항:</p>
 * <ul>
 *   <li>토큰 암호화 알고리즘 설정</li>
 *   <li>시크릿 키 관리</li>
 *   <li>토큰 갱신 정책</li>
 * </ul>
 */
@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public String getSecret() {
        return secret;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
} 