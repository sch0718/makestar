package com.makestar.history.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 시스템 에러 로그를 저장하는 엔티티 클래스입니다.
 * 모든 서비스에서 발생하는 에러와 예외 상황을 기록하고 추적합니다.
 */
@Entity
@Table(name = "error_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLog {

    /**
     * 에러 로그의 고유 식별자
     * UUID 형식으로 자동 생성됩니다.
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    /**
     * 에러가 발생한 서비스의 이름
     * 마이크로서비스 아키텍처에서 어떤 서비스에서 에러가 발생했는지 식별합니다.
     */
    @Column(nullable = false)
    private String serviceName;
    
    /**
     * 에러의 심각도 레벨
     * @see ErrorLevel
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ErrorLevel errorLevel;
    
    /**
     * 에러의 고유 코드
     * 특정 유형의 에러를 식별하는데 사용됩니다.
     */
    @Column
    private String errorCode;
    
    /**
     * 에러 메시지
     * 에러에 대한 상세 설명을 포함합니다.
     */
    @Column(columnDefinition = "TEXT")
    private String message;
    
    /**
     * 에러 발생 시의 스택 트레이스
     * 디버깅을 위한 상세 정보를 포함합니다.
     */
    @Column(columnDefinition = "TEXT")
    private String stackTrace;
    
    /**
     * 에러가 발생했을 때의 사용자 ID
     * 인증된 사용자의 경우에만 기록됩니다.
     */
    @Column
    private String userId;
    
    /**
     * 에러가 발생한 요청의 URL
     */
    @Column
    private String requestUrl;
    
    /**
     * 에러가 발생한 요청의 HTTP 메소드
     */
    @Column
    private String requestMethod;
    
    /**
     * 에러가 발생한 요청의 파라미터들
     * JSON 형식으로 저장됩니다.
     */
    @Column(columnDefinition = "TEXT")
    private String requestParams;
    
    /**
     * 에러가 발생한 요청의 IP 주소
     */
    @Column
    private String ipAddress;
    
    /**
     * 에러 발생 시간
     * 기본값으로 현재 시간이 설정됩니다.
     */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * 에러의 심각도 레벨을 정의하는 열거형
     */
    public enum ErrorLevel {
        INFO,       // 정보성 메시지
        WARNING,    // 경고 메시지
        ERROR,      // 일반 에러
        CRITICAL    // 심각한 에러
    }
} 