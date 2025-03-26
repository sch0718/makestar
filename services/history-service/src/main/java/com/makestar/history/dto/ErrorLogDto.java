package com.makestar.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.makestar.history.model.ErrorLog;

/**
 * 에러 로그 정보를 전송하기 위한 DTO 클래스
 * 마이크로서비스 간 에러 정보 전달에 사용됩니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLogDto {
    private String id;                // 에러 로그 고유 식별자
    private String serviceName;       // 에러가 발생한 서비스 이름
    private String errorLevel;        // 에러 수준 (예: INFO, WARN, ERROR, FATAL)
    private String errorCode;         // 에러 코드
    private String message;           // 에러 메시지
    private String stackTrace;        // 스택 트레이스 정보
    private String userId;            // 에러 발생 시 사용자 ID
    private String requestUrl;        // 요청 URL
    private String requestMethod;     // HTTP 메소드 (GET, POST, PUT, DELETE 등)
    private String requestParams;     // 요청 파라미터
    private String ipAddress;         // 요청자 IP 주소
    private LocalDateTime timestamp;  // 에러 발생 시간
    
    // 추가 정보
    private String username;          // 사용자 이름 (사용자 서비스에서 조회)
    
    /**
     * ErrorLog 엔티티를 DTO로 변환하는 정적 메소드
     * 
     * @param errorLog 변환할 ErrorLog 엔티티
     * @return 변환된 ErrorLogDto 객체
     */
    public static ErrorLogDto fromEntity(ErrorLog errorLog) {
        return ErrorLogDto.builder()
                .id(errorLog.getId())
                .serviceName(errorLog.getServiceName())
                .errorLevel(errorLog.getErrorLevel().name())
                .errorCode(errorLog.getErrorCode())
                .message(errorLog.getMessage())
                .stackTrace(errorLog.getStackTrace())
                .userId(errorLog.getUserId())
                .requestUrl(errorLog.getRequestUrl())
                .requestMethod(errorLog.getRequestMethod())
                .requestParams(errorLog.getRequestParams())
                .ipAddress(errorLog.getIpAddress())
                .timestamp(errorLog.getTimestamp())
                .build();
    }
    
    /**
     * ErrorLog 엔티티와 사용자 이름을 함께 DTO로 변환하는 정적 메소드
     * 
     * @param errorLog 변환할 ErrorLog 엔티티
     * @param username 사용자 이름
     * @return 사용자 이름이 포함된 ErrorLogDto 객체
     */
    public static ErrorLogDto fromEntityWithUsername(ErrorLog errorLog, String username) {
        ErrorLogDto dto = fromEntity(errorLog);
        dto.setUsername(username);
        return dto;
    }
} 