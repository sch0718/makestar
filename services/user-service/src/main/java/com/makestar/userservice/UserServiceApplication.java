package com.makestar.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * User Service 애플리케이션의 메인 클래스입니다.
 * 사용자 관리, 친구 관계 관리 등의 기능을 제공하는 마이크로서비스입니다.
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 정보 관리 (생성, 조회, 수정, 삭제)</li>
 *   <li>친구 관계 관리 (친구 추가, 삭제, 조회)</li>
 *   <li>친구 요청 관리 (요청 생성, 수락, 거절)</li>
 * </ul>
 *
 * <p>사용된 Spring Cloud 기능:</p>
 * <ul>
 *   <li>Eureka Client: 서비스 디스커버리를 위한 Eureka 클라이언트 활성화</li>
 *   <li>Feign Client: 다른 마이크로서비스와의 통신을 위한 선언적 REST 클라이언트 활성화</li>
 * </ul>
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class UserServiceApplication {

    /**
     * 애플리케이션의 진입점입니다.
     * Spring Boot 애플리케이션을 실행합니다.
     *
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
} 