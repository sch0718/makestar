package com.makestar.chatservice.dto;

import com.makestar.chatservice.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String id;
    private String chatRoomId;
    private String senderId;
    private String senderName; // 추가 정보 (사용자 서비스에서 조회)
    private String content;
    private String type;
    private LocalDateTime sentAt;
    private boolean read;
    private LocalDateTime readAt;

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

    public static ChatMessageDto fromEntityWithSenderName(ChatMessage chatMessage, String senderName) {
        ChatMessageDto dto = fromEntity(chatMessage);
        dto.setSenderName(senderName);
        return dto;
    }
} 