package com.makestar.apigateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * API Gateway 라우팅 및 회로 차단기 구성 클래스
 * 
 * <p>Spring Cloud Gateway의 라우팅 규칙, 필터, 회로 차단기 등을 정의합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>서비스 라우팅 설정</li>
 *   <li>글로벌 필터 구성</li>
 *   <li>로드밸런싱 설정</li>
 *   <li>서비스 디스커버리 통합</li>
 *   <li>회로 차단기 구성</li>
 * </ul>
 */
@Configuration
public class GatewayConfig {
    
    /**
     * Resilience4J 기반 회로 차단기 팩토리 사용자 정의
     * 
     * <p>회로 차단기 설정:</p>
     * <ul>
     *   <li>실패율 50% 이상일 때 회로 열림</li>
     *   <li>슬라이딩 윈도우 크기: 10</li>
     *   <li>열린 상태 지속 시간: 10초</li>
     *   <li>타임아웃: 3초</li>
     * </ul>
     *
     * @return Customizer 회로 차단기 팩토리 사용자 정의
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .circuitBreakerConfig(CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .build())
            .timeLimiterConfig(TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(3))
                .build())
            .build());
    }
} 