package com.makestar.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 인증 서비스 애플리케이션
 * 
 * <p>MakeStar 플랫폼의 인증 서비스를 담당하는 Spring Boot 애플리케이션입니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 인증 및 인가</li>
 *   <li>JWT 토큰 관리</li>
 *   <li>회원 관리</li>
 * </ul>
 */
@SpringBootApplication
@EnableEurekaClient
@EntityScan(basePackages = {"com.makestar.auth.entity", "com.makestar.commons.model"})
@EnableJpaRepositories(basePackages = {"com.makestar.auth.repository"})
@ComponentScan(basePackages = {
    "com.makestar.auth", 
    "com.makestar.commons.utils",
    "com.makestar.commons.config"
})
public class AuthServiceApplication {

    /**
     * 인증 서비스 애플리케이션의 진입점
     * 
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
} 