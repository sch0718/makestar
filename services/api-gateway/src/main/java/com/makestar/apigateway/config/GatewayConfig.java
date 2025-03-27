package com.makestar.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * API Gateway 구성 클래스
 * 
 * <p>Spring Cloud Gateway의 라우팅 규칙, 필터, 보안 설정 등을 정의합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>서비스 라우팅 설정</li>
 *   <li>글로벌 필터 구성</li>
 *   <li>보안 정책 적용</li>
 *   <li>로드밸런싱 설정</li>
 *   <li>서비스 디스커버리 통합</li>
 * </ul>
 */
// @Configuration
public class GatewayConfig {
    
    /**
     * CORS(Cross-Origin Resource Sharing) 설정을 위한 필터 빈을 생성합니다.
     * 
     * <p>주요 설정:</p>
     * <ul>
     *   <li>모든 출처(Origin)에 대한 요청 허용</li> 
     *   <li>preflight 요청 캐시 시간을 3600초(1시간)로 설정</li>
     *   <li>GET, POST, PUT, DELETE, OPTIONS 메서드 허용</li>
     *   <li>Content-Type, Authorization 헤더 허용</li>
     * </ul>
     *
     * @return CorsWebFilter CORS 설정이 적용된 웹 필터
     */
    // @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Collections.singletonList("*"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
} 