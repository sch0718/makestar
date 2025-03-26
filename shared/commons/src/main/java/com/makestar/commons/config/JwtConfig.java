package com.makestar.commons.config;

import org.springframework.beans.factory.annotation.Value;
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
     * JWT 시크릿 키를 반환합니다.
     *
     * @return JWT 서명에 사용되는 시크릿 키
     */
    public String getSecret() {
        return secret;
    }

    /**
     * JWT 시크릿 키를 설정합니다.
     *
     * @param secret JWT 서명에 사용할 시크릿 키
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * 액세스 토큰의 만료 시간을 반환합니다.
     *
     * @return 액세스 토큰 만료 시간 (밀리초)
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * 액세스 토큰의 만료 시간을 설정합니다.
     *
     * @param accessTokenExpiration 설정할 액세스 토큰 만료 시간 (밀리초)
     */
    public void setAccessTokenExpiration(long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    /**
     * 리프레시 토큰의 만료 시간을 반환합니다.
     *
     * @return 리프레시 토큰 만료 시간 (밀리초)
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    /**
     * 리프레시 토큰의 만료 시간을 설정합니다.
     *
     * @param refreshTokenExpiration 설정할 리프레시 토큰 만료 시간 (밀리초)
     */
    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
} 