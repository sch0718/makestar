package com.makestar.chatservice.dto;

import com.makestar.chatservice.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 데이터 전송 객체 (DTO)
 * 클라이언트와 서버 간의 채팅 메시지 데이터 교환을 위한 클래스입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    /** 메시지 고유 식별자 */
    private String id;
    /** 메시지가 속한 채팅방 ID */
    private String chatRoomId;
    /** 메시지 발신자 ID */
    private String senderId;
    /** 메시지 발신자 이름 (사용자 서비스에서 조회) */
    private String senderName;
    /** 메시지 내용 */
    private String content;
    /** 메시지 타입 */
    private String type;
    /** 메시지 전송 시간 */
    private LocalDateTime sentAt;
    /** 메시지 읽음 여부 */
    private boolean read;
    /** 메시지 읽은 시간 */
    private LocalDateTime readAt;

    /**
     * ChatMessage 엔티티를 DTO로 변환합니다.
     * 
     * @param chatMessage 변환할 채팅 메시지 엔티티
     * @return 변환된 ChatMessageDto 객체
     */
    public static ChatMessageDto fromEntity(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .id(chatMessage.getId())
                .chatRoomId(chatMessage.getChatRoomId())
                .senderId(chatMessage.getSenderId())
                .content(chatMessage.getContent())
                .type(chatMessage.getType().name())
                .sentAt(chatMessage.getSentAt())
                .read(chatMessage.isRead())
                .readAt(chatMessage.getReadAt())
                .build();
    }

    /**
     * ChatMessage 엔티티를 DTO로 변환하고 발신자 이름을 설정합니다.
     * 
     * @param chatMessage 변환할 채팅 메시지 엔티티
     * @param senderName 발신자 이름
     * @return 변환된 ChatMessageDto 객체
     */
    public static ChatMessageDto fromEntityWithSenderName(ChatMessage chatMessage, String senderName) {
        ChatMessageDto dto = fromEntity(chatMessage);
        dto.setSenderName(senderName);
        return dto;
    }
} 