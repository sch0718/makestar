package com.makestar.historyservice.dto;

import com.makestar.historyservice.model.ErrorLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLogDto {
    private String id;
    private String serviceName;
    private String errorLevel;
    private String errorCode;
    private String message;
    private String stackTrace;
    private String userId;
    private String requestUrl;
    private String requestMethod;
    private String requestParams;
    private String ipAddress;
    private LocalDateTime timestamp;
    
    // 추가 정보
    private String username;
    
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
    
    public static ErrorLogDto fromEntityWithUsername(ErrorLog errorLog, String username) {
        ErrorLogDto dto = fromEntity(errorLog);
        dto.setUsername(username);
        return dto;
    }
} 