package com.makestar.historyservice.repository;

import com.makestar.historyservice.model.ActivityHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityHistoryRepository extends JpaRepository<ActivityHistory, String> {

    // 특정 사용자의 활동 내역 조회
    Page<ActivityHistory> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);
    
    // 특정 사용자의 특정 활동 유형 내역 조회
    Page<ActivityHistory> findByUserIdAndActivityTypeOrderByTimestampDesc(String userId, ActivityHistory.ActivityType activityType, Pageable pageable);
    
    // 특정 활동 유형의 내역 조회
    Page<ActivityHistory> findByActivityTypeOrderByTimestampDesc(ActivityHistory.ActivityType activityType, Pageable pageable);
    
    // 특정 시간 이후의 활동 내역 조회
    List<ActivityHistory> findByTimestampAfterOrderByTimestampDesc(LocalDateTime timestamp);
    
    // 특정 엔티티에 대한 활동 내역 조회
    List<ActivityHistory> findByEntityIdAndEntityTypeOrderByTimestampDesc(String entityId, String entityType);
    
    // 특정 사용자의 최근 활동 내역 조회
    @Query("SELECT a FROM ActivityHistory a WHERE a.userId = :userId ORDER BY a.timestamp DESC")
    List<ActivityHistory> findRecentActivitiesByUser(@Param("userId") String userId, Pageable pageable);
    
    // 활동 유형별 사용자 수 조회
    @Query("SELECT a.activityType, COUNT(DISTINCT a.userId) FROM ActivityHistory a " +
           "WHERE a.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY a.activityType")
    List<Object[]> countUsersByActivityType(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 