package com.makestar.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 인증 필터 컴포넌트
 * 
 * <p>API Gateway에서 들어오는 모든 요청에 대해 JWT 토큰을 검증하고 인증을 처리하는 필터입니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>요청 헤더에서 JWT 토큰 추출</li>
 *   <li>토큰 유효성 검증</li>
 *   <li>토큰 만료 여부 확인</li>
 *   <li>사용자 인증 정보 설정</li>
 *   <li>인증 예외 처리</li>
 * </ul>
 * 
 * <p>처리 흐름:</p>
 * <ol>
 *   <li>Authorization 헤더 확인</li>
 *   <li>Bearer 토큰 추출</li>
 *   <li>JWT 토큰 검증</li>
 *   <li>클레임 정보 추출</li>
 *   <li>인증 컨텍스트 설정</li>
 * </ol>
 * 
 * <p>예외 처리:</p>
 * <ul>
 *   <li>토큰 누락 예외</li>
 *   <li>토큰 만료 예외</li>
 *   <li>토큰 형식 예외</li>
 *   <li>서명 검증 예외</li>
 * </ul>
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT 인증 필터 생성자
     * 
     * <p>부모 클래스의 생성자를 호출하여 Config 클래스를 설정합니다.</p>
     */
    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    /**
     * Gateway 필터 적용 메소드
     * 
     * <p>요청에 대한 JWT 토큰 검증을 수행하고 인증을 처리합니다.</p>
     * 
     * <p>처리 과정:</p>
     * <ol>
     *   <li>인증이 필요없는 엔드포인트 확인 (/api/auth/login, /api/auth/register, /chat-ws)</li>
     *   <li>Authorization 헤더 존재 여부 확인</li>
     *   <li>Bearer 토큰 형식 검증</li>
     *   <li>JWT 토큰 유효성 검증</li>
     *   <li>토큰 만료 여부 확인</li>
     *   <li>사용자 정보를 요청 헤더에 추가</li>
     * </ol>
     *
     * @param config 필터 설정 객체
     * @return {@link GatewayFilter} JWT 인증을 처리하는 게이트웨이 필터
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Skip JWT validation for authentication endpoints
            if (request.getURI().getPath().contains("/api/auth/login") ||
                request.getURI().getPath().contains("/api/auth/register") ||
                request.getURI().getPath().contains("/chat-ws") ||
                request.getURI().getPath().contains("/actuator") ||
                request.getURI().getPath().contains("/api-docs")) {
                return chain.filter(exchange);
            }
            
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }
            
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
            }
            
            String token = authHeader.substring(7);
            
            try {
                // Validate JWT token
                Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                
                // Check if token is expired
                if (claims.getExpiration().before(new Date())) {
                    return onError(exchange, "JWT token is expired", HttpStatus.UNAUTHORIZED);
                }
                
                // Add username to request header
                String username = claims.getSubject();
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-Auth-User", username)
                    .build();
                
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception e) {
                log.error("JWT token validation error: {}", e.getMessage());
                return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    /**
     * 에러 응답 처리 메소드
     * 
     * <p>인증 과정에서 발생한 오류를 처리하고 적절한 HTTP 응답을 반환합니다.</p>
     *
     * @param exchange 서버 웹 교환 객체
     * @param message 에러 메시지
     * @param status HTTP 상태 코드
     * @return {@link Mono<Void>} 완료된 응답
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    /**
     * JWT 필터 설정 클래스
     * 
     * <p>필터에 필요한 설정 속성을 정의합니다.</p>
     * <p>현재는 추가 설정이 필요하지 않아 비어있는 상태입니다.</p>
     */
    public static class Config {
        // Put the configuration properties if needed
    }
} 