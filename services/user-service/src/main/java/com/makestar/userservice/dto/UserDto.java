package com.makestar.userservice.dto;

import com.makestar.userservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 정보를 전달하기 위한 DTO(Data Transfer Object) 클래스입니다.
 * 사용자의 기본 정보와 상태, 시간 정보, 친구 목록을 포함합니다.
 * 
 * <p>포함하는 정보:</p>
 * <ul>
 *   <li>기본 정보 (ID, 사용자명, 이메일)</li>
 *   <li>상태 정보 (현재 상태)</li>
 *   <li>시간 정보 (마지막 접속, 생성, 수정 시간)</li>
 *   <li>관계 정보 (친구 ID 목록)</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    /** 사용자의 고유 식별자 */
    private String id;
    
    /** 사용자의 고유한 사용자명 */
    private String username;
    
    /** 사용자의 이메일 주소 */
    private String email;
    
    /** 
     * 사용자의 현재 상태
     * 가능한 값: ONLINE(온라인), OFFLINE(오프라인), AWAY(자리비움), BUSY(다른 용무중)
     */
    private String status;
    
    /** 사용자의 마지막 접속 시간 */
    private LocalDateTime lastSeen;
    
    /** 사용자 계정 생성 시간 */
    private LocalDateTime createdAt;
    
    /** 사용자 정보 마지막 수정 시간 */
    private LocalDateTime updatedAt;
    
    /** 사용자의 친구 ID 목록 */
    private List<String> friendIds;
    
    /**
     * User 엔티티를 UserDto로 변환합니다.
     * 엔티티의 모든 필드를 DTO에 매핑하고, 친구 목록은 ID 목록으로 변환합니다.
     * 
     * @param user 변환할 User 엔티티
     * @return 변환된 UserDto 객체
     */
    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .lastSeen(user.getLastSeen())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .friendIds(user.getFriends().stream()
                        .map(User::getId)
                        .collect(Collectors.toList()))
                .build();
    }
} 