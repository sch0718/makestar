package com.makestar.history.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.makestar.history.dto.ActivityHistoryDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 사용자 활동 이력을 관리하는 서비스 인터페이스입니다.
 * 사용자의 모든 활동을 기록하고, 조회하며, 분석하는 기능을 제공합니다.
 */
public interface ActivityHistoryService {

    /**
     * 새로운 사용자 활동을 기록합니다.
     * 
     * @param activityHistoryDto 기록할 활동 정보를 담은 DTO
     * @return 저장된 활동 정보
     */
    ActivityHistoryDto recordActivity(ActivityHistoryDto activityHistoryDto);
    
    /**
     * 특정 사용자의 전체 활동 내역을 페이지 단위로 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이지네이션 정보
     * @return 사용자의 활동 내역 페이지
     */
    Page<ActivityHistoryDto> getUserActivityHistory(String userId, Pageable pageable);
    
    /**
     * 특정 사용자의 특정 유형의 활동 내역을 페이지 단위로 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @param activityType 조회할 활동 유형
     * @param pageable 페이지네이션 정보
     * @return 특정 유형의 사용자 활동 내역 페이지
     */
    Page<ActivityHistoryDto> getUserActivityHistoryByType(String userId, String activityType, Pageable pageable);
    
    /**
     * 특정 활동 유형의 전체 내역을 페이지 단위로 조회합니다.
     * 
     * @param activityType 조회할 활동 유형
     * @param pageable 페이지네이션 정보
     * @return 특정 유형의 전체 활동 내역 페이지
     */
    Page<ActivityHistoryDto> getActivityHistoryByType(String activityType, Pageable pageable);
    
    /**
     * 특정 시점 이후의 모든 활동 내역을 조회합니다.
     * 
     * @param timestamp 기준 시간
     * @return 기준 시간 이후의 활동 내역 목록
     */
    List<ActivityHistoryDto> getActivitiesSince(LocalDateTime timestamp);
    
    /**
     * 특정 엔티티에 대한 모든 활동 내역을 조회합니다.
     * 
     * @param entityId 조회할 엔티티의 ID
     * @param entityType 조회할 엔티티의 유형
     * @return 해당 엔티티에 대한 활동 내역 목록
     */
    List<ActivityHistoryDto> getActivitiesByEntity(String entityId, String entityType);
    
    /**
     * 특정 사용자의 최근 활동 내역을 제한된 수만큼 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @param limit 조회할 활동 내역의 최대 개수
     * @return 사용자의 최근 활동 내역 목록
     */
    List<ActivityHistoryDto> getRecentActivitiesByUser(String userId, int limit);
    
    /**
     * 특정 기간 동안의 활동 유형별 사용자 수를 조회합니다.
     * 
     * @param startDate 조회 시작 일시
     * @param endDate 조회 종료 일시
     * @return 활동 유형별 사용자 수 맵 (키: 활동 유형, 값: 사용자 수)
     */
    Map<String, Long> getUserCountByActivityType(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 특정 사용자의 모든 활동 내역을 삭제합니다.
     * 주로 데이터 정리나 개인정보 보호를 위해 사용됩니다.
     * 
     * @param userId 삭제할 활동 내역의 사용자 ID
     */
    void deleteUserActivities(String userId);
} 