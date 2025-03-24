package com.makestar.chatservice.dto;

import com.makestar.chatservice.model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private String id;
    private String name;
    private String description;
    private String type;
    private Set<String> participantIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ChatMessageDto lastMessage;
    private long unreadCount;

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

    public static ChatRoomDto fromEntityWithLastMessage(ChatRoom chatRoom, ChatMessageDto lastMessage, long unreadCount) {
        ChatRoomDto dto = fromEntity(chatRoom);
        dto.setLastMessage(lastMessage);
        dto.setUnreadCount(unreadCount);
        return dto;
    }
} 