package com.makestar.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
/**
 * 디스커버리 서비스 애플리케이션
 * 
 * <p>Spring Cloud Netflix Eureka Server를 사용한 서비스 디스커버리의 메인 애플리케이션 클래스입니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>마이크로서비스 등록</li>
 *   <li>서비스 위치 정보 관리</li>
 *   <li>서비스 상태 모니터링</li>
 *   <li>로드 밸런싱 지원</li>
 * </ul>
 */
public class DiscoveryServiceApplication {

    /**
     * 디스커버리 서비스 애플리케이션의 진입점
     * 
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
} 