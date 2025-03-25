package com.makestar.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 친구 요청 정보를 전달하기 위한 DTO(Data Transfer Object) 클래스입니다.
 * 친구 요청의 상태와 관련된 정보를 클라이언트와 주고받을 때 사용됩니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto {
    /** 친구 요청의 고유 식별자 */
    private String id;
    
    /** 친구 요청을 보낸 사용자의 ID */
    private String senderId;
    
    /** 친구 요청을 받은 사용자의 ID */
    private String receiverId;
    
    /** 
     * 친구 요청의 현재 상태
     * 가능한 값: PENDING(대기중), ACCEPTED(수락됨), REJECTED(거절됨)
     */
    private String status;
    
    /** 친구 요청이 생성된 시간 */
    private LocalDateTime createdAt;
    
    /** 친구 요청이 마지막으로 수정된 시간 */
    private LocalDateTime updatedAt;
} 