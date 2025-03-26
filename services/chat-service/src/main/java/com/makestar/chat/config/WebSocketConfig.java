package com.makestar.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 통신을 위한 설정 클래스입니다.
 * STOMP 프로토콜을 사용하여 실시간 양방향 통신을 구현합니다.
 * 클라이언트와 서버 간의 WebSocket 연결, 메시지 브로커 설정을 담당합니다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * WebSocket 연결을 위한 엔드포인트를 등록합니다.
     * SockJS를 통해 WebSocket을 지원하지 않는 브라우저에서도 동작하도록 설정합니다.
     *
     * @param registry STOMP 엔드포인트 등록을 위한 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // CORS 설정
                .withSockJS();  // SockJS 지원 활성화
    }

    /**
     * 메시지 브로커를 설정합니다.
     * 메시지 라우팅, 구독 주제(topic) 등을 정의합니다.
     * 
     * - /topic, /queue: 구독 가능한 주제에 대한 prefix
     * - /app: 클라이언트에서 메시지 발행 시 사용할 prefix
     * - /user: 특정 사용자에게 메시지 발송 시 사용할 prefix
     *
     * @param registry 메시지 브로커 설정을 위한 레지스트리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
} 