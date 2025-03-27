package com.makestar.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * MakeStar 유저 서비스의 메인 애플리케이션 클래스입니다.
 * 이 클래스는 Spring Boot 애플리케이션의 시작점이며,
 * Eureka Client와 Feign Client 기능을 활성화합니다.
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 정보 관리</li>
 *   <li>친구 관계 관리</li>
 *   <li>친구 요청 관리</li>
 * </ul>
 * 
 * @EnableEurekaClient: 서비스 디스커버리를 위한 Eureka Client 활성화
 * @EnableFeignClients: 서비스 간 통신을 위한 Feign Client 활성화
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EntityScan(basePackages = {"com.makestar.user.model", "com.makestar.user.entity", "com.makestar.commons.model"})
@ComponentScan(basePackages = {
    "com.makestar.user",
    "com.makestar.commons.utils",
    "com.makestar.commons.config"
})
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