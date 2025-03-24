package com.makestar.historyservice.service;

import com.makestar.historyservice.dto.ErrorLogDto;
import com.makestar.historyservice.model.ErrorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ErrorLogService {

    // 오류 로그 저장
    ErrorLogDto logError(ErrorLogDto errorLogDto);
    
    // 서비스별 오류 로그 조회
    Page<ErrorLogDto> getErrorsByService(String serviceName, Pageable pageable);
    
    // 오류 수준별 로그 조회
    Page<ErrorLogDto> getErrorsByLevel(String errorLevel, Pageable pageable);
    
    // 특정 사용자 관련 오류 로그 조회
    Page<ErrorLogDto> getErrorsByUser(String userId, Pageable pageable);
    
    // 특정 시간 이후의 오류 로그 조회
    List<ErrorLogDto> getErrorsSince(LocalDateTime timestamp);
    
    // 특정 오류 코드의 로그 조회
    List<ErrorLogDto> getErrorsByCode(String errorCode);
    
    // 서비스별 오류 통계 조회
    Map<String, Long> getErrorCountByService(LocalDateTime startDate, LocalDateTime endDate);
    
    // 오류 수준별 통계 조회
    Map<String, Long> getErrorCountByLevel(LocalDateTime startDate, LocalDateTime endDate);
    
    // 가장 빈번한 오류 코드 조회
    List<Map<String, Object>> getMostFrequentErrors(LocalDateTime startDate, LocalDateTime endDate, int limit);
    
    // 오류 로그 삭제 (데이터 정리 또는 개인정보 보호 목적)
    void deleteOldErrorLogs(LocalDateTime before);
} 