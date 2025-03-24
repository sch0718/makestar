package com.makestar.historyservice.service;

import com.makestar.historyservice.dto.ActivityHistoryDto;
import com.makestar.historyservice.model.ActivityHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ActivityHistoryService {

    // 활동 내역 저장
    ActivityHistoryDto recordActivity(ActivityHistoryDto activityHistoryDto);
    
    // 특정 사용자의 활동 내역 조회
    Page<ActivityHistoryDto> getUserActivityHistory(String userId, Pageable pageable);
    
    // 특정 사용자의 특정 활동 유형 내역 조회
    Page<ActivityHistoryDto> getUserActivityHistoryByType(String userId, String activityType, Pageable pageable);
    
    // 특정 활동 유형의 내역 조회
    Page<ActivityHistoryDto> getActivityHistoryByType(String activityType, Pageable pageable);
    
    // 특정 시간 이후의 활동 내역 조회
    List<ActivityHistoryDto> getActivitiesSince(LocalDateTime timestamp);
    
    // 특정 엔티티에 대한 활동 내역 조회
    List<ActivityHistoryDto> getActivitiesByEntity(String entityId, String entityType);
    
    // 특정 사용자의 최근 활동 내역 조회
    List<ActivityHistoryDto> getRecentActivitiesByUser(String userId, int limit);
    
    // 활동 유형별 사용자 수 조회
    Map<String, Long> getUserCountByActivityType(LocalDateTime startDate, LocalDateTime endDate);
    
    // 사용자 활동 삭제 (데이터 정리 또는 개인정보 보호 목적)
    void deleteUserActivities(String userId);
} 