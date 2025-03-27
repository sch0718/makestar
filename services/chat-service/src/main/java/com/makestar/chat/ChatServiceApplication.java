package com.makestar.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * MakeStar 채팅 서비스의 메인 애플리케이션 클래스입니다.
 * 이 클래스는 Spring Boot 애플리케이션의 시작점이며,
 * Eureka Client와 Feign Client 기능을 활성화합니다.
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>채팅 기능 제공</li>
 *   <li>채팅방 관리</li>
 *   <li>메시지 전송 및 관리</li>
 * </ul>
 * 
 * @EnableEurekaClient: 서비스 디스커버리를 위한 Eureka Client 활성화
 * @EnableFeignClients: 서비스 간 통신을 위한 Feign Client 활성화
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EntityScan(basePackages = {"com.makestar.chat.model", "com.makestar.chat.entity", "com.makestar.commons.model"})
@ComponentScan(basePackages = {
    "com.makestar.chat",
    "com.makestar.commons.utils",
    "com.makestar.commons.config"
})
public class ChatServiceApplication {

    /**
     * 애플리케이션의 진입점입니다.
     * Spring Boot 애플리케이션을 실행합니다.
     *
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }
} 