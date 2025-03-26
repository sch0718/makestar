package com.makestar.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.makestar.commons.config.JwtConfig;
import com.makestar.commons.utils.jwt.JwtUtils;

/**
 * JWT 관련 빈 설정 클래스
 */
@Configuration
public class JwBeanConfig {

    private final JwtConfig jwtConfig;
    
    /**
     * 생성자 주입을 통해 commons 모듈의 JwtConfig를 주입받습니다.
     * 
     * @param jwtConfig JWT 설정 객체
     */
    public JwBeanConfig(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }
    
    /**
     * JwtUtils 빈을 생성합니다.
     * commons 모듈의 JwtConfig에서 설정값을 가져와 JwtUtils를 초기화합니다.
     * 
     * @return 설정된 JwtUtils 객체
     */
    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(
            jwtConfig.getSecret(), 
            jwtConfig.getAccessTokenExpiration(), 
            jwtConfig.getRefreshTokenExpiration()
        );
    }
} 