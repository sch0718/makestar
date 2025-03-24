package com.makestar.dto.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageDto {
    
    private String id;
    
    @NotBlank(message = "채팅방 ID는 필수 입력값입니다.")
    private String chatRoomId;
    
    @NotBlank(message = "발신자 ID는 필수 입력값입니다.")
    private String senderId;
    
    private String senderName;
    
    @NotNull(message = "메시지 타입은 필수 입력값입니다.")
    private MessageType type;
    
    @NotBlank(message = "메시지 내용은 필수 입력값입니다.")
    @Size(max = 1000, message = "메시지 내용은 최대 1000자까지 입력 가능합니다.")
    private String content;
    
    private LocalDateTime timestamp;
    
    private MessageStatus status;
    
    // 메시지 타입 열거형
    public enum MessageType {
        CHAT, JOIN, LEAVE, SYSTEM
    }
    
    // 메시지 상태 열거형
    public enum MessageStatus {
        SENT, DELIVERED, READ
    }
} 