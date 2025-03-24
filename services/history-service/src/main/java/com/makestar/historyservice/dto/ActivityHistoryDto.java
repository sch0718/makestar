package com.makestar.historyservice.dto;

import com.makestar.historyservice.model.ActivityHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityHistoryDto {
    private String id;
    private String userId;
    private String activityType;
    private String entityId;
    private String entityType;
    private String description;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String userAgent;
    
    // 추가 정보
    private String username;
    
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
    
    public static ActivityHistoryDto fromEntityWithUsername(ActivityHistory activityHistory, String username) {
        ActivityHistoryDto dto = fromEntity(activityHistory);
        dto.setUsername(username);
        return dto;
    }
} 