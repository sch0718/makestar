package com.makestar.historyservice.service;

import com.makestar.historyservice.dto.ErrorLogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 시스템 에러 로그를 관리하는 서비스 인터페이스입니다.
 * 모든 서비스의 에러를 기록하고, 조회하며, 분석하는 기능을 제공합니다.
 */
public interface ErrorLogService {

    /**
     * 새로운 에러 로그를 기록합니다.
     * 
     * @param errorLogDto 기록할 에러 정보를 담은 DTO
     * @return 저장된 에러 로그 정보
     */
    ErrorLogDto logError(ErrorLogDto errorLogDto);
    
    /**
     * 특정 서비스의 에러 로그를 페이지 단위로 조회합니다.
     * 
     * @param serviceName 조회할 서비스 이름
     * @param pageable 페이지네이션 정보
     * @return 서비스의 에러 로그 페이지
     */
    Page<ErrorLogDto> getErrorsByService(String serviceName, Pageable pageable);
    
    /**
     * 특정 에러 수준의 로그를 페이지 단위로 조회합니다.
     * 
     * @param errorLevel 조회할 에러 수준 (INFO, WARNING, ERROR, CRITICAL)
     * @param pageable 페이지네이션 정보
     * @return 특정 수준의 에러 로그 페이지
     */
    Page<ErrorLogDto> getErrorsByLevel(String errorLevel, Pageable pageable);
    
    /**
     * 특정 사용자와 관련된 에러 로그를 페이지 단위로 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이지네이션 정보
     * @return 사용자 관련 에러 로그 페이지
     */
    Page<ErrorLogDto> getErrorsByUser(String userId, Pageable pageable);
    
    /**
     * 특정 시점 이후의 모든 에러 로그를 조회합니다.
     * 
     * @param timestamp 기준 시간
     * @return 기준 시간 이후의 에러 로그 목록
     */
    List<ErrorLogDto> getErrorsSince(LocalDateTime timestamp);
    
    /**
     * 특정 에러 코드의 모든 로그를 조회합니다.
     * 
     * @param errorCode 조회할 에러 코드
     * @return 해당 에러 코드의 로그 목록
     */
    List<ErrorLogDto> getErrorsByCode(String errorCode);
    
    /**
     * 특정 기간 동안의 서비스별 에러 발생 횟수를 조회합니다.
     * 
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @return 서비스별 에러 횟수 맵 (키: 서비스 이름, 값: 에러 횟수)
     */
    Map<String, Long> getErrorCountByService(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 특정 기간 동안의 에러 수준별 발생 횟수를 조회합니다.
     * 
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @return 에러 수준별 횟수 맵 (키: 에러 수준, 값: 발생 횟수)
     */
    Map<String, Long> getErrorCountByLevel(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 특정 기간 동안 가장 빈번하게 발생한 에러를 조회합니다.
     * 
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @param limit 조회할 에러의 최대 개수
     * @return 빈번한 에러 목록 (에러 코드, 발생 횟수, 마지막 발생 시간 등 포함)
     */
    List<Map<String, Object>> getMostFrequentErrors(LocalDateTime startDate, LocalDateTime endDate, int limit);
    
    /**
     * 특정 시점 이전의 오래된 에러 로그를 삭제합니다.
     * 주로 데이터 정리나 스토리지 관리를 위해 사용됩니다.
     * 
     * @param before 이 시점 이전의 로그를 삭제
     */
    void deleteOldErrorLogs(LocalDateTime before);
} 