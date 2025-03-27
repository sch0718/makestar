package com.makestar.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.SimpMessageType;

/**
 * WebSocket 보안 설정 클래스
 * 
 * <p>웹소켓 연결의 보안 설정을 담당합니다.</p>
 * <p>CSRF 보호를 비활성화하고 웹소켓 메시지 보안 규칙을 설정합니다.</p>
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    /**
     * CSRF 보호를 비활성화합니다.
     * WebSocket 연결 시 CSRF 토큰 검증이 필요하지 않도록 설정합니다.
     * 
     * @return CSRF 보호 비활성화 여부
     */
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    /**
     * WebSocket 메시지에 대한 보안 규칙을 구성합니다.
     * 메시지 유형과 목적지에 따라 인증 요구사항을 세밀하게 설정합니다.
     * 
     * @param messages 메시지 보안 메타데이터 소스 레지스트리
     */
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        // 모든 메시지 유형에 대해 접근 허용 (개발 과정에서 문제 해결을 위해)
        messages
            // 연결, 구독, 연결 해제는 모든 사용자 허용
            .simpTypeMatchers(SimpMessageType.CONNECT, 
                              SimpMessageType.SUBSCRIBE, 
                              SimpMessageType.DISCONNECT).permitAll()
            // 모든 목적지에 대한 메시지 전송 허용
            .simpDestMatchers("/app/**", "/topic/**", "/queue/**", "/user/**").permitAll()
            // 기타 모든 메시지도 허용
            .anyMessage().permitAll();
    }
} 