package com.makestar.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

/**
 * API 게이트웨이 애플리케이션
 * 
 * <p>Spring Cloud Gateway를 사용한 API 게이트웨이 서비스의 메인 애플리케이션 클래스입니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>마이크로서비스 라우팅</li>
 *   <li>요청/응답 필터링</li>
 *   <li>로드 밸런싱</li>
 *   <li>인증/인가 처리</li>
 *   <li>서비스 디스커버리 통합</li>
 * </ul>
 * 
 * <p>실행 환경:</p>
 * <ul>
 *   <li>Spring Boot 기반</li>
 *   <li>Spring Cloud Gateway</li>
 *   <li>Spring Cloud Netflix Eureka Client</li>
 * </ul>
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    WebMvcAutoConfiguration.class
})
public class ApiGatewayApplication {

    /**
     * API 게이트웨이 애플리케이션의 진입점
     * 
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
} 