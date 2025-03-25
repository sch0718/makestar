package com.makestar.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * API 게이트웨이 로깅 필터
 * 
 * <p>모든 요청과 응답에 대한 로깅을 처리하는 글로벌 필터입니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>요청/응답 시간 기록</li>
 *   <li>요청 메소드 및 URI 로깅</li>
 *   <li>응답 상태 코드 로깅</li>
 *   <li>요청 처리 소요 시간 측정</li>
 * </ul>
 * 
 * <p>로그 포맷:</p>
 * <ul>
 *   <li>요청 시작: "Request {requestId} initiated: {method} {uri}"</li>
 *   <li>요청 완료: "Request {requestId} completed: {method} {uri} with status {statusCode} in {duration} ms"</li>
 * </ul>
 */
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    /**
     * 필터 처리 메소드
     * 
     * <p>요청과 응답에 대한 로깅을 수행합니다.</p>
     * 
     * <p>처리 과정:</p>
     * <ol>
     *   <li>고유한 요청 ID 생성</li>
     *   <li>요청 시작 시간 기록</li>
     *   <li>요청 정보 로깅</li>
     *   <li>체인 필터 실행</li>
     *   <li>응답 정보 및 처리 시간 로깅</li>
     * </ol>
     *
     * @param exchange 서버 웹 교환 객체
     * @param chain 게이트웨이 필터 체인
     * @return {@link Mono<Void>} 필터 체인 실행 결과
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = UUID.randomUUID().toString();
        
        log.info("Request {} initiated: {} {}", requestId, request.getMethod(), request.getURI());
        
        long startTime = System.currentTimeMillis();
        
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    log.info("Request {} completed: {} {} with status {} in {} ms", 
                            requestId, 
                            request.getMethod(), 
                            request.getURI(), 
                            exchange.getResponse().getStatusCode(), 
                            duration);
                }));
    }

    /**
     * 필터 우선순위 설정 메소드
     * 
     * <p>이 필터가 다른 필터들보다 먼저 실행되도록 최상위 우선순위를 설정합니다.</p>
     * 
     * @return {@link int} 필터 우선순위 값 (HIGHEST_PRECEDENCE)
     */
    @Override
    public int getOrder() {
        // Set to highest precedence (executed first)
        return Ordered.HIGHEST_PRECEDENCE;
    }
} 