package com.makestar.chatservice.dto;

import lombok.Data;
import java.util.Set;

/**
 * 채팅방 생성 요청 DTO
 * 새로운 채팅방 생성에 필요한 정보를 담고 있습니다.
 */
@Data
public class CreateChatRoomRequest {
    /** 채팅방 이름 */
    private String name;
    /** 채팅방 설명 */
    private String description;
    /** 채팅방 타입 (개인/그룹) */
    private String type;
    /** 채팅방 생성자 ID */
    private String creatorId;
    /** 초기 참여자 ID 목록 */
    private Set<String> participantIds;
} 