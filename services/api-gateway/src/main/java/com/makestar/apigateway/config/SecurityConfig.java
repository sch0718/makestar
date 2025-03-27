package com.makestar.apigateway.config;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
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
 *   <li>경로 기반 보안 규칙 적용</li>
 *   <li>공용/인증된 리소스 접근 제어</li>
 *   <li>CORS 설정</li>
 *   <li>레이트 리미팅 키 리졸버</li>
 * </ul>
 */
@Configuration
@Slf4j
public class SecurityConfig {
    
    @Value("${app.security.csrf.excluded-paths:}")
    private List<String> csrfExcludedPaths;
    
    @Value("${app.security.csrf.enabled:true}")
    private boolean csrfEnabled;
    
    /**
     * 레이트 리미팅을 위한 키 리졸버를 설정합니다.
     * 기본적으로 요청자의 IP 주소를 키로 사용합니다.
     * 
     * @return KeyResolver IP 주소 기반 키 리졸버
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
            Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                .map(address -> address.getAddress().getHostAddress())
                .orElse("unknown")
        );
    }
    
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
        // CSRF 설정
        if (!csrfEnabled) {
            // CSRF 보호 완전 비활성화
            log.info("CSRF 보호가 비활성화되었습니다.");
            http.csrf(csrf -> csrf.disable());
        } else if (!CollectionUtils.isEmpty(csrfExcludedPaths)) {
            log.info("다음 경로는 CSRF 보호에서 제외됩니다: " + csrfExcludedPaths);
            
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
        
        // 경로 기반 보안 규칙 설정
        http.authorizeExchange(exchanges -> exchanges
            // 공용 엔드포인트 (인증 필요 없음)
            .pathMatchers("/api/auth/**", "/chat-ws/**", "/actuator/**", "/api-docs/**").permitAll()
            // Swagger/OpenAPI 관련 경로 허용
            .pathMatchers("/api-docs", "/api-docs/**", "/swagger-ui.html", "/webjars/**").permitAll()
            // 정적 리소스 허용
            .pathMatchers("/", "/favicon.ico", "/css/**", "/js/**").permitAll()
            // OPTIONS 요청 모두 허용 (CORS preflight 요청)
            .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // 모든 요청은 인증 필요
            .anyExchange().authenticated()
        );
        
        // CORS 설정 명시적으로 활성화
        // http.cors();
        
        // JWT 필터를 통해 인증 처리되므로 기본 폼 로그인과 HTTP Basic 인증 비활성화
        http
            // HTTP Basic 인증 명시적으로 비활성화
            .httpBasic(httpBasic -> httpBasic.disable())
            // 폼 로그인 비활성화
            .formLogin(formLogin -> formLogin.disable())
            // 로그아웃 기능 비활성화 (JWT 기반 인증 사용)
            .logout(logout -> logout.disable());
        
        return http.build();
    }
} 