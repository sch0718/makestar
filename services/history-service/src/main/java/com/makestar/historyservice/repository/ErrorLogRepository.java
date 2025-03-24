package com.makestar.historyservice.repository;

import com.makestar.historyservice.model.ErrorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, String> {

    // 서비스별 오류 로그 조회
    Page<ErrorLog> findByServiceNameOrderByTimestampDesc(String serviceName, Pageable pageable);
    
    // 오류 수준별 로그 조회
    Page<ErrorLog> findByErrorLevelOrderByTimestampDesc(ErrorLog.ErrorLevel errorLevel, Pageable pageable);
    
    // 특정 사용자 관련 오류 로그 조회
    Page<ErrorLog> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);
    
    // 특정 시간 이후의 오류 로그 조회
    List<ErrorLog> findByTimestampAfterOrderByTimestampDesc(LocalDateTime timestamp);
    
    // 특정 오류 코드의 로그 조회
    List<ErrorLog> findByErrorCodeOrderByTimestampDesc(String errorCode);
    
    // 서비스별 오류 통계 조회
    @Query("SELECT e.serviceName, COUNT(e) FROM ErrorLog e " +
           "WHERE e.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY e.serviceName")
    List<Object[]> countErrorsByService(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 오류 수준별 통계 조회
    @Query("SELECT e.errorLevel, COUNT(e) FROM ErrorLog e " +
           "WHERE e.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY e.errorLevel")
    List<Object[]> countErrorsByLevel(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 특정 오류 코드의 발생 빈도 조회
    @Query("SELECT e.errorCode, COUNT(e) FROM ErrorLog e " +
           "WHERE e.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY e.errorCode " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> findMostFrequentErrors(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
} 