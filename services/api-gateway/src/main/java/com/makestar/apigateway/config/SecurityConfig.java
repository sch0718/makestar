package com.makestar.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API 게이트웨이 보안 설정 클래스
 * 
 * <p>Spring Security WebFlux 기반 보안 설정을 정의합니다.</p>
 * 
 * <p>주요 설정:</p>
 * <ul>
 *   <li>인증 관련 엔드포인트에 CSRF 보호 제외</li>
 *   <li>WebSocket 연결에 CSRF 보호 제외</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {
    
    @Value("${app.security.csrf.excluded-paths:}")
    private List<String> csrfExcludedPaths;
    
    /**
     * 보안 필터 체인 구성
     * 
     * <p>WebFlux 보안 설정을 정의하고 필터 체인을 구성합니다.</p>
     * 
     * @param http HTTP 보안 설정 객체
     * @return 구성된 SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // application.yml에서 설정한 경로 패턴을 사용하여 CSRF 제외 경로 구성
        if (csrfExcludedPaths == null || csrfExcludedPaths.isEmpty()) {
            // 기본값: /api/auth/** 및 /chat-ws/**
            csrfExcludedPaths = List.of("/api/auth/**", "/chat-ws/**");
        }
        
        // 모든 제외 경로에 대한 매처 생성
        List<ServerWebExchangeMatcher> matchers = csrfExcludedPaths.stream()
            .map(PathPatternParserServerWebExchangeMatcher::new)
            .collect(Collectors.toList());
        
        // 다중 매처 조합
        ServerWebExchangeMatcher csrfExcludedPathsMatcher;
        if (matchers.size() == 1) {
            csrfExcludedPathsMatcher = matchers.get(0);
        } else {
            csrfExcludedPathsMatcher = new OrServerWebExchangeMatcher(matchers);
        }
        
        // 지정된 경로를 제외한 나머지에만 CSRF 적용
        http
            .csrf(csrf -> csrf
                .requireCsrfProtectionMatcher(new NegatedServerWebExchangeMatcher(csrfExcludedPathsMatcher)));
        
        return http.build();
    }
} 