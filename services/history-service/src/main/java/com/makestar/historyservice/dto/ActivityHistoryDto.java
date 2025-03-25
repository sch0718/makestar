package com.makestar.historyservice.dto;

import com.makestar.historyservice.model.ActivityHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 활동 이력을 전송하기 위한 DTO(Data Transfer Object) 클래스입니다.
 * 사용자의 다양한 활동 정보를 클라이언트에게 전달하거나 서비스 간 통신에 사용됩니다.
 * 
 * <p>주요 정보:</p>
 * <ul>
 *   <li>사용자 식별 정보 (ID, 사용자명)</li>
 *   <li>활동 상세 정보 (활동 유형, 대상 엔티티, 설명)</li>
 *   <li>시간 및 환경 정보 (타임스탬프, IP 주소, 사용자 에이전트)</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityHistoryDto {
    /** 활동 이력의 고유 식별자 */
    private String id;
    
    /** 활동을 수행한 사용자의 ID */
    private String userId;
    
    /** 수행된 활동의 유형 (예: CREATE, UPDATE, DELETE 등) */
    private String activityType;
    
    /** 활동의 대상이 된 엔티티의 ID */
    private String entityId;
    
    /** 활동의 대상이 된 엔티티의 유형 */
    private String entityType;
    
    /** 활동에 대한 상세 설명 */
    private String description;
    
    /** 활동이 발생한 시간 */
    private LocalDateTime timestamp;
    
    /** 활동이 발생한 IP 주소 */
    private String ipAddress;
    
    /** 활동에 사용된 사용자 에이전트 정보 */
    private String userAgent;
    
    /** 활동을 수행한 사용자의 이름 (추가 정보) */
    private String username;
    
    /**
     * ActivityHistory 엔티티를 DTO로 변환합니다.
     * 
     * @param activityHistory 변환할 ActivityHistory 엔티티
     * @return 변환된 ActivityHistoryDto 객체
     */
    public static ActivityHistoryDto fromEntity(ActivityHistory activityHistory) {
        return ActivityHistoryDto.builder()
                .id(activityHistory.getId())
                .userId(activityHistory.getUserId())
                .activityType(activityHistory.getActivityType().name())
                .entityId(activityHistory.getEntityId())
                .entityType(activityHistory.getEntityType())
                .description(activityHistory.getDescription())
                .timestamp(activityHistory.getTimestamp())
                .ipAddress(activityHistory.getIpAddress())
                .userAgent(activityHistory.getUserAgent())
                .build();
    }
    
    /**
     * ActivityHistory 엔티티를 DTO로 변환하면서 사용자 이름을 추가합니다.
     * 
     * @param activityHistory 변환할 ActivityHistory 엔티티
     * @param username 추가할 사용자 이름
     * @return 사용자 이름이 포함된 ActivityHistoryDto 객체
     */
    public static ActivityHistoryDto fromEntityWithUsername(ActivityHistory activityHistory, String username) {
        ActivityHistoryDto dto = fromEntity(activityHistory);
        dto.setUsername(username);
        return dto;
    }
} 