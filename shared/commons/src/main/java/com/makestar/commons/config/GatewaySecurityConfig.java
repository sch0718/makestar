package com.makestar.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 게이트웨이 보안 설정 클래스
 * 
 * <p>모든 마이크로서비스에 공통으로 적용되는 보안 설정을 정의합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>보안 필터 체인 구성</li>
 *   <li>GatewayAuthenticationFilter 적용</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class GatewaySecurityConfig {

    /**
     * 공통 보안 필터 체인 설정
     * 
     * <p>모든 마이크로서비스에서 사용할 기본 보안 설정을 구성합니다.</p>
     */
    @Bean
    @Order(2) // 서비스별 보안 설정보다 낮은 우선순위
    public SecurityFilterChain gatewaySecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/actuator/**", "/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(gatewayAuthenticationFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public GatewayAuthenticationFilter gatewayAuthenticationFilter() {
        return new GatewayAuthenticationFilter();
    }
} 