package com.makestar.apigateway.config;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.util.CollectionUtils;

/**
 * API 게이트웨이 보안 설정 클래스
 * 
 * <p>Spring Security WebFlux 기반 보안 설정을 정의합니다.</p>
 * 
 * <p>주요 설정:</p>
 * <ul>
 *   <li>인증 관련 엔드포인트에 CSRF 보호 제외</li>
 *   <li>WebSocket 연결에 CSRF 보호 제외</li>
 *   <li>Actuator 엔드포인트에 CSRF 보호 제외</li>
 *   <li>CSRF 보호 활성화/비활성화 옵션</li>
 * </ul>
 */
// @Configuration
public class SecurityConfig {
    
    @Value("${app.security.csrf.excluded-paths:}")
    private List<String> csrfExcludedPaths;
    
    @Value("${app.security.csrf.enabled:true}")
    private boolean csrfEnabled;
    
    /**
     * 보안 필터 체인 구성
     * 
     * <p>WebFlux 보안 설정을 정의하고 필터 체인을 구성합니다.</p>
     * 
     * @param http HTTP 보안 설정 객체
     * @return 구성된 SecurityWebFilterChain
     */
    // @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // CSRF 설정
        if (!csrfEnabled) {
            // CSRF 보호 완전 비활성화
            System.out.println("CSRF 보호가 비활성화되었습니다.");
            http.csrf(csrf -> csrf.disable());
        } else if (!CollectionUtils.isEmpty(csrfExcludedPaths)) {
            System.out.println("다음 경로는 CSRF 보호에서 제외됩니다: " + csrfExcludedPaths);
            
            // CSRF 제외 경로에 대한 매처 생성
            List<ServerWebExchangeMatcher> matchers = new ArrayList<>();
            for (String pattern : csrfExcludedPaths) {
                matchers.add(new PathPatternParserServerWebExchangeMatcher(pattern));
            }
            
            // 여러 매처를 OR 연산으로 결합
            ServerWebExchangeMatcher csrfMatcher = new OrServerWebExchangeMatcher(matchers);
            
            // CSRF 설정: 지정된 경로에서는 CSRF 보호 제외, 나머지는 활성화
            http.csrf((csrf) -> csrf
                .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                .requireCsrfProtectionMatcher(new NegatedServerWebExchangeMatcher(csrfMatcher))
            );
        } else {
            // 제외 경로가 없으면 모든 요청에 CSRF 보호 적용
            http.csrf((csrf) -> csrf
                .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
            );
        }
        
        // CORS 설정 유지
        http.cors().disable();
        
        return http.build();
    }
} 