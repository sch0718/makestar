package com.makestar.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket 통신을 위한 설정 클래스입니다.
 * STOMP 프로토콜을 사용하여 실시간 양방향 통신을 구현합니다.
 * 클라이언트와 서버 간의 WebSocket 연결, 메시지 브로커 설정을 담당합니다.
 */
@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * WebSocket 하트비트를 위한 TaskScheduler 빈을 생성합니다.
     * 이 스케줄러는 클라이언트와 서버 간의 하트비트 메시지를 주기적으로 전송하는 데 사용됩니다.
     * 
     * @return 스레드 풀 기반 TaskScheduler 인스턴스
     */
    @Bean
    public TaskScheduler webSocketHeartbeatTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("ws-heartbeat-thread-");
        return taskScheduler;
    }

    /**
     * WebSocket 연결을 위한 엔드포인트를 등록합니다.
     * SockJS를 통해 WebSocket을 지원하지 않는 브라우저에서도 동작하도록 설정합니다.
     *
     * @param registry STOMP 엔드포인트 등록을 위한 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/chat-ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setSupressCors(true)
                .setSessionCookieNeeded(false)
                .setHeartbeatTime(25000);
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
        registry.enableSimpleBroker("/topic", "/queue")
               .setHeartbeatValue(new long[] {10000, 10000}) // 클라이언트와 서버 하트비트 간격 설정
               .setTaskScheduler(webSocketHeartbeatTaskScheduler()); // 하트비트 스케줄러 설정
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
    
    /**
     * WebSocket 전송 설정을 구성합니다.
     * 메시지 크기 제한, 시간 제한 등을 설정합니다.
     *
     * @param registration WebSocket 전송 등록 객체
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(64 * 1024) // 64KB
                   .setSendBufferSizeLimit(512 * 1024) // 512KB
                   .setSendTimeLimit(20000); // 20 seconds
    }
} 