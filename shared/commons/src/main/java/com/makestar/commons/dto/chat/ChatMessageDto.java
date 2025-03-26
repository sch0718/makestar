package com.makestar.commons.dto.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 채팅 메시지 정보를 전달하기 위한 DTO(Data Transfer Object) 클래스입니다.
 * 채팅방에서 주고받는 메시지의 내용과 메타데이터를 포함합니다.
 * 
 * <p>포함하는 정보:</p>
 * <ul>
 *   <li>메시지 식별 정보 (ID, 채팅방 ID)</li>
 *   <li>발신자 정보 (ID, 이름)</li>
 *   <li>메시지 내용 및 타입</li>
 *   <li>시간 정보</li>
 *   <li>메시지 상태</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageDto {
    
    /** 메시지의 고유 식별자 */
    private String id;
    
    /** 
     * 메시지가 속한 채팅방의 ID
     * 공백이나 null이 허용되지 않습니다.
     */
    @NotBlank(message = "채팅방 ID는 필수 입력값입니다.")
    private String chatRoomId;
    
    /** 
     * 메시지를 보낸 사용자의 ID
     * 공백이나 null이 허용되지 않습니다.
     */
    @NotBlank(message = "발신자 ID는 필수 입력값입니다.")
    private String senderId;
    
    /** 메시지를 보낸 사용자의 이름 */
    private String senderName;
    
    /** 
     * 메시지의 타입
     * null이 허용되지 않습니다.
     */
    @NotNull(message = "메시지 타입은 필수 입력값입니다.")
    private MessageType type;
    
    /** 
     * 메시지의 실제 내용
     * 공백이나 null이 허용되지 않으며, 최대 1000자까지 입력 가능합니다.
     */
    @NotBlank(message = "메시지 내용은 필수 입력값입니다.")
    @Size(max = 1000, message = "메시지 내용은 최대 1000자까지 입력 가능합니다.")
    private String content;
    
    /** 메시지가 생성된 시간 */
    private LocalDateTime timestamp;
    
    /** 메시지의 현재 상태 (전송됨, 전달됨, 읽음) */
    private MessageStatus status;
    
    /**
     * 메시지 타입을 정의하는 열거형
     * CHAT: 일반 채팅 메시지
     * JOIN: 사용자 입장 메시지
     * LEAVE: 사용자 퇴장 메시지
     * SYSTEM: 시스템 메시지
     */
    public enum MessageType {
        CHAT, JOIN, LEAVE, SYSTEM
    }
    
    /**
     * 메시지 상태를 정의하는 열거형
     * SENT: 메시지가 전송됨
     * DELIVERED: 메시지가 상대방에게 전달됨
     * READ: 메시지가 읽힘
     */
    public enum MessageStatus {
        SENT, DELIVERED, READ
    }
} 