package com.makestar.history.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.makestar.history.model.ActivityHistory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 활동 이력 데이터에 대한 데이터베이스 접근을 담당하는 리포지토리입니다.
 * Spring Data JPA를 사용하여 ActivityHistory 엔티티의 CRUD 작업을 처리합니다.
 */
@Repository
public interface ActivityHistoryRepository extends JpaRepository<ActivityHistory, String> {

    /**
     * 특정 사용자의 모든 활동 내역을 시간 역순으로 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이지네이션 정보
     * @return 사용자의 활동 내역 페이지
     */
    Page<ActivityHistory> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);
    
    /**
     * 특정 사용자의 특정 활동 유형에 대한 내역을 시간 역순으로 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @param activityType 조회할 활동 유형
     * @param pageable 페이지네이션 정보
     * @return 사용자의 특정 활동 유형 내역 페이지
     */
    Page<ActivityHistory> findByUserIdAndActivityTypeOrderByTimestampDesc(String userId, ActivityHistory.ActivityType activityType, Pageable pageable);
    
    /**
     * 특정 활동 유형의 모든 내역을 시간 역순으로 조회합니다.
     * 
     * @param activityType 조회할 활동 유형
     * @param pageable 페이지네이션 정보
     * @return 특정 활동 유형의 전체 내역 페이지
     */
    Page<ActivityHistory> findByActivityTypeOrderByTimestampDesc(ActivityHistory.ActivityType activityType, Pageable pageable);
    
    /**
     * 특정 시점 이후의 모든 활동 내역을 시간 역순으로 조회합니다.
     * 
     * @param timestamp 기준 시간
     * @return 기준 시간 이후의 활동 내역 목록
     */
    List<ActivityHistory> findByTimestampAfterOrderByTimestampDesc(LocalDateTime timestamp);
    
    /**
     * 특정 엔티티에 대한 모든 활동 내역을 시간 역순으로 조회합니다.
     * 
     * @param entityId 조회할 엔티티의 ID
     * @param entityType 조회할 엔티티의 유형
     * @return 해당 엔티티에 대한 활동 내역 목록
     */
    List<ActivityHistory> findByEntityIdAndEntityTypeOrderByTimestampDesc(String entityId, String entityType);
    
    /**
     * 특정 사용자의 최근 활동 내역을 조회합니다.
     * JPQL을 사용하여 사용자별 최근 활동을 페이지 단위로 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이지네이션 정보 (결과 제한에 사용)
     * @return 사용자의 최근 활동 내역 목록
     */
    @Query("SELECT a FROM ActivityHistory a WHERE a.userId = :userId ORDER BY a.timestamp DESC")
    List<ActivityHistory> findRecentActivitiesByUser(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 특정 기간 동안의 활동 유형별 고유 사용자 수를 조회합니다.
     * JPQL을 사용하여 활동 유형별로 그룹화된 사용자 통계를 생성합니다.
     * 
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @return 활동 유형과 해당 활동을 수행한 고유 사용자 수의 배열 목록
     */
    @Query("SELECT a.activityType, COUNT(DISTINCT a.userId) FROM ActivityHistory a " +
           "WHERE a.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY a.activityType")
    List<Object[]> countUsersByActivityType(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 