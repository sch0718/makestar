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
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomDto {
    
    private String id;
    
    @NotBlank(message = "채팅방 이름은 필수 입력값입니다.")
    @Size(min = 2, max = 100, message = "채팅방 이름은 2자 이상 100자 이하로 입력해주세요.")
    private String name;
    
    @NotNull(message = "채팅방 타입은 필수 입력값입니다.")
    private RoomType type;
    
    private String creatorId;
    
    @Builder.Default
    private Set<String> participantIds = new HashSet<>();
    
    @Size(max = 200, message = "채팅방 설명은 최대 200자까지 입력 가능합니다.")
    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String lastMessageId;
    
    private LocalDateTime lastMessageTime;
    
    // 채팅방 타입 열거형
    public enum RoomType {
        DIRECT, // 1:1 채팅
        GROUP    // 그룹 채팅
    }
} 