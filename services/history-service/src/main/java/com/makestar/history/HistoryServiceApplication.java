package com.makestar.history;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * MakeStar 히스토리 서비스의 메인 애플리케이션 클래스입니다.
 * 이 클래스는 Spring Boot 애플리케이션의 시작점이며,
 * 사용자 활동 및 시스템 이벤트의 이력을 관리합니다.
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 활동 이력 기록</li>
 *   <li>시스템 이벤트 로깅</li>
 *   <li>이력 데이터 조회 및 분석</li>
 * </ul>
 *
 * @EnableEurekaClient: 서비스 디스커버리를 위한 Eureka Client 활성화
 * @EnableFeignClients: 서비스 간 통신을 위한 Feign Client 활성화
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EntityScan(basePackages = {"com.makestar.history.model", "com.makestar.history.entity", "com.makestar.commons.model"})
@ComponentScan(basePackages = {
    "com.makestar.history",
    "com.makestar.commons.utils",
    "com.makestar.commons.config"
})
public class HistoryServiceApplication {

    /**
     * 히스토리 서비스 애플리케이션의 진입점
     * 
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(HistoryServiceApplication.class, args);
    }
} 