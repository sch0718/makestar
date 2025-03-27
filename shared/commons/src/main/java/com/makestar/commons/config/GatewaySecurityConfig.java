package com.makestar.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

/**
 * 게이트웨이 보안 설정 클래스
 * 
 * <p>모든 마이크로서비스에 공통으로 적용되는 보안 설정을 정의합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>보안 필터 체인 구성</li>
 *   <li>GatewayAuthenticationFilter 적용</li>
 *   <li>공통 요청 권한 설정</li>
 *   <li>CSRF 및 세션 관리 설정</li>
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
                // CSRF 보호 비활성화 (API 서버이므로)
                .csrf(csrf -> csrf.disable())
                // CORS 설정 비활성화 (API 게이트웨이에서 처리)
                .cors(cors -> cors.disable())
                // 세션 관리 정책 설정 - STATELESS(상태 비저장)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 요청 권한 설정
                .authorizeRequests(auth -> auth
                    // 인증 관련 엔드포인트는 인증 없이 접근 가능
                    .antMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh-token").permitAll()
                    // Actuator 엔드포인트 접근 허용
                    .antMatchers("/actuator/**").permitAll()
                    // API 문서 접근 허용
                    .antMatchers("/api-docs/**", "/swagger-ui/**").permitAll()
                    // OPTIONS 요청 허용 (CORS preflight)
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // 기타 모든 요청은 인증 필요
                    .anyRequest().authenticated())
                // HTTP Basic 인증 비활성화
                .httpBasic(httpBasic -> httpBasic.disable())
                // 폼 로그인 비활성화
                .formLogin(formLogin -> formLogin.disable())
                // 게이트웨이 인증 필터 추가
                .addFilterBefore(gatewayAuthenticationFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public GatewayAuthenticationFilter gatewayAuthenticationFilter() {
        return new GatewayAuthenticationFilter();
    }
} 