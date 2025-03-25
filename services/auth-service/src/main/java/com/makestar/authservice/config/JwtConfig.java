package com.makestar.authservice.config;

import com.makestar.utils.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 설정 클래스
 * 
 * <p>JWT(JSON Web Token) 관련 설정을 정의하는 클래스입니다.</p>
 * 
 * <p>설정 항목:</p>
 * <ul>
 *   <li>JWT 시크릿 키</li>
 *   <li>액세스 토큰 만료 시간</li>
 *   <li>리프레시 토큰 만료 시간</li>
 * </ul>
 */
@Configuration
public class JwtConfig {

    /**
     * JWT 서명에 사용되는 시크릿 키
     */
    @Value("${jwt.secret}")
    private String secret;
    
    /**
     * 액세스 토큰의 만료 시간 (밀리초)
     */
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;
    
    /**
     * 리프레시 토큰의 만료 시간 (밀리초)
     */
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    
    /**
     * JWT 유틸리티 빈 설정
     * 
     * @return 구성된 JwtUtils 인스턴스
     */
    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(secret, accessTokenExpiration, refreshTokenExpiration);
    }
} 