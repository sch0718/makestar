package com.makestar.history.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.makestar.history.model.ErrorLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 시스템 에러 로그 데이터에 대한 데이터베이스 접근을 담당하는 리포지토리입니다.
 * Spring Data JPA를 사용하여 ErrorLog 엔티티의 CRUD 작업을 처리합니다.
 */
@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, String> {

    /**
     * 특정 서비스의 모든 에러 로그를 시간 역순으로 조회합니다.
     * 
     * @param serviceName 조회할 서비스의 이름
     * @param pageable 페이지네이션 정보
     * @return 서비스의 에러 로그 페이지
     */
    Page<ErrorLog> findByServiceNameOrderByTimestampDesc(String serviceName, Pageable pageable);
    
    /**
     * 특정 에러 수준의 모든 로그를 시간 역순으로 조회합니다.
     * 
     * @param errorLevel 조회할 에러 수준 (INFO, WARNING, ERROR, CRITICAL)
     * @param pageable 페이지네이션 정보
     * @return 특정 수준의 에러 로그 페이지
     */
    Page<ErrorLog> findByErrorLevelOrderByTimestampDesc(ErrorLog.ErrorLevel errorLevel, Pageable pageable);
    
    /**
     * 특정 사용자와 관련된 모든 에러 로그를 시간 역순으로 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이지네이션 정보
     * @return 사용자 관련 에러 로그 페이지
     */
    Page<ErrorLog> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);
    
    /**
     * 특정 시점 이후의 모든 에러 로그를 시간 역순으로 조회합니다.
     * 
     * @param timestamp 기준 시간
     * @return 기준 시간 이후의 에러 로그 목록
     */
    List<ErrorLog> findByTimestampAfterOrderByTimestampDesc(LocalDateTime timestamp);
    
    /**
     * 특정 에러 코드의 모든 로그를 시간 역순으로 조회합니다.
     * 
     * @param errorCode 조회할 에러 코드
     * @return 해당 에러 코드의 로그 목록
     */
    List<ErrorLog> findByErrorCodeOrderByTimestampDesc(String errorCode);
    
    /**
     * 특정 기간 동안의 서비스별 에러 발생 횟수를 조회합니다.
     * JPQL을 사용하여 서비스별로 그룹화된 에러 통계를 생성합니다.
     * 
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @return 서비스 이름과 해당 서비스의 에러 발생 횟수의 배열 목록
     */
    @Query("SELECT e.serviceName, COUNT(e) FROM ErrorLog e " +
           "WHERE e.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY e.serviceName")
    List<Object[]> countErrorsByService(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 기간 동안의 에러 수준별 발생 횟수를 조회합니다.
     * JPQL을 사용하여 에러 수준별로 그룹화된 통계를 생성합니다.
     * 
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @return 에러 수준과 해당 수준의 에러 발생 횟수의 배열 목록
     */
    @Query("SELECT e.errorLevel, COUNT(e) FROM ErrorLog e " +
           "WHERE e.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY e.errorLevel")
    List<Object[]> countErrorsByLevel(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 기간 동안 가장 빈번하게 발생한 에러 코드를 조회합니다.
     * JPQL을 사용하여 에러 코드별 발생 빈도를 계산하고 내림차순으로 정렬합니다.
     * 
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @param pageable 페이지네이션 정보 (결과 제한에 사용)
     * @return 에러 코드와 해당 코드의 발생 횟수의 배열 목록
     */
    @Query("SELECT e.errorCode, COUNT(e) FROM ErrorLog e " +
           "WHERE e.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY e.errorCode " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> findMostFrequentErrors(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
} 