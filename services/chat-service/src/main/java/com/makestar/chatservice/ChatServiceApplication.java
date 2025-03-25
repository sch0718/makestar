package com.makestar.chatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * MakeStar 채팅 서비스의 메인 애플리케이션 클래스입니다.
 * 이 클래스는 Spring Boot 애플리케이션의 시작점이며,
 * Eureka Client와 Feign Client 기능을 활성화합니다.
 *
 * @EnableEurekaClient: 서비스 디스커버리를 위한 Eureka Client 활성화
 * @EnableFeignClients: 서비스 간 통신을 위한 Feign Client 활성화
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class ChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }
} 