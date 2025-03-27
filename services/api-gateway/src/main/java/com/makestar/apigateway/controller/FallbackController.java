package com.makestar.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 서비스 폴백 컨트롤러
 * 
 * <p>회로 차단기가 열렸을 때 (서비스가 응답하지 않을 때) 대체 응답을 제공합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>마이크로서비스 장애 시 사용자 친화적인 오류 메시지 제공</li>
 *   <li>각 서비스별 맞춤형 폴백 응답 구현</li>
 *   <li>서비스 가용성 상태 정보 포함</li>
 * </ul>
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * 인증 서비스 폴백
     * 
     * @return 인증 서비스 장애 시 대체 응답
     */
    @GetMapping("/auth-service")
    public ResponseEntity<Map<String, Object>> authServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "인증 서비스를 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");
        response.put("service", "auth-service");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    /**
     * 사용자 서비스 폴백
     * 
     * @return 사용자 서비스 장애 시 대체 응답
     */
    @GetMapping("/user-service")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "사용자 서비스를 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");
        response.put("service", "user-service");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    /**
     * 채팅 서비스 폴백
     * 
     * @return 채팅 서비스 장애 시 대체 응답
     */
    @GetMapping("/chat-service")
    public ResponseEntity<Map<String, Object>> chatServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "채팅 서비스를 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");
        response.put("service", "chat-service");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    /**
     * 히스토리 서비스 폴백
     * 
     * @return 히스토리 서비스 장애 시 대체 응답
     */
    @GetMapping("/history-service")
    public ResponseEntity<Map<String, Object>> historyServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "히스토리 서비스를 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");
        response.put("service", "history-service");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
} 