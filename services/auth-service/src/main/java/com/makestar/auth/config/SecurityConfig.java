package com.makestar.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 설정 클래스
 * 
 * <p>인증 서비스의 보안 관련 설정을 정의합니다.</p>
 * 
 * <p>주요 설정:</p>
 * <ul>
 *   <li>보안 필터 체인 구성</li>
 *   <li>비밀번호 인코더 설정</li>
 *   <li>인증 관리자 설정</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {
    
    /**
     * 비밀번호 인코더 빈 설정
     * 
     * @return BCrypt 비밀번호 인코더 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 인증 관리자 빈 설정
     * 
     * @param authConfig 인증 설정 객체
     * @return 구성된 AuthenticationManager
     * @throws Exception 인증 관리자 생성 중 발생할 수 있는 예외
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
} 