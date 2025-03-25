package com.makestar.chatservice.dto;

import lombok.Data;
import java.util.Set;

/**
 * 채팅방 참여자 추가 요청 DTO
 * 채팅방에 새로운 참여자를 추가하기 위한 정보를 담고 있습니다.
 */
@Data
public class AddParticipantsRequest {
    /** 추가할 참여자 ID 목록 */
    private Set<String> participantIds;
} 