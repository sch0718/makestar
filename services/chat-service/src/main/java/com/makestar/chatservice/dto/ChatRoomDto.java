package com.makestar.chatservice.dto;

import com.makestar.chatservice.model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 채팅방 데이터 전송 객체 (DTO)
 * 클라이언트와 서버 간의 채팅방 정보 교환을 위한 클래스입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    /** 채팅방 고유 식별자 */
    private String id;
    /** 채팅방 이름 */
    private String name;
    /** 채팅방 생성자 ID */
    private String creatorId;
    /** 채팅방 생성자 이름 */
    private String creatorName;
    /** 채팅방 참여자 ID 목록 */
    private Set<String> participantIds;
    /** 채팅방 참여자 이름 목록 */
    private List<String> participantNames;
    /** 채팅방 생성 시간 */
    private LocalDateTime createdAt;
    /** 마지막 메시지 시간 */
    private LocalDateTime lastMessageAt;
    /** 채팅방 타입 (개인/그룹) */
    private String type;
    private String description;
    private LocalDateTime updatedAt;
    private ChatMessageDto lastMessage;
    private long unreadCount;

    /**
     * ChatRoom 엔티티를 DTO로 변환합니다.
     * 
     * @param chatRoom 변환할 채팅방 엔티티
     * @return 변환된 ChatRoomDto 객체
     */
    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .description(chatRoom.getDescription())
                .type(chatRoom.getType().name())
                .participantIds(chatRoom.getParticipantIds())
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
    }

    /**
     * ChatRoom 엔티티를 DTO로 변환하고 참여자 정보를 설정합니다.
     * 
     * @param chatRoom 변환할 채팅방 엔티티
     * @param participantNames 참여자 이름 목록
     * @return 변환된 ChatRoomDto 객체
     */
    public static ChatRoomDto fromEntityWithParticipants(ChatRoom chatRoom, List<String> participantNames) {
        ChatRoomDto dto = fromEntity(chatRoom);
        dto.setParticipantNames(participantNames);
        return dto;
    }

    public static ChatRoomDto fromEntityWithLastMessage(ChatRoom chatRoom, ChatMessageDto lastMessage, long unreadCount) {
        ChatRoomDto dto = fromEntity(chatRoom);
        dto.setLastMessage(lastMessage);
        dto.setUnreadCount(unreadCount);
        return dto;
    }
} 