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

/**
 * 채팅방 정보를 전달하기 위한 DTO(Data Transfer Object) 클래스입니다.
 * 채팅방의 기본 정보와 참여자, 메시지 관련 메타데이터를 포함합니다.
 * 
 * <p>포함하는 정보:</p>
 * <ul>
 *   <li>채팅방 기본 정보 (ID, 이름, 설명, 타입)</li>
 *   <li>생성 정보 (생성자 ID, 생성 시간)</li>
 *   <li>참여자 정보 (참여자 ID 목록)</li>
 *   <li>최근 메시지 정보 (마지막 메시지 ID, 시간)</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomDto {
    
    /** 채팅방의 고유 식별자 */
    private String id;
    
    /** 
     * 채팅방의 이름
     * 2자 이상 100자 이하로 입력해야 하며, 공백이나 null이 허용되지 않습니다.
     */
    @NotBlank(message = "채팅방 이름은 필수 입력값입니다.")
    @Size(min = 2, max = 100, message = "채팅방 이름은 2자 이상 100자 이하로 입력해주세요.")
    private String name;
    
    /** 
     * 채팅방의 타입
     * DIRECT(1:1 채팅) 또는 GROUP(그룹 채팅)이 가능합니다.
     */
    @NotNull(message = "채팅방 타입은 필수 입력값입니다.")
    private RoomType type;
    
    /** 채팅방을 생성한 사용자의 ID */
    private String creatorId;
    
    /** 
     * 채팅방 참여자들의 ID 목록
     * HashSet을 사용하여 중복을 방지합니다.
     */
    @Builder.Default
    private Set<String> participantIds = new HashSet<>();
    
    /** 
     * 채팅방에 대한 설명
     * 최대 200자까지 입력 가능합니다.
     */
    @Size(max = 200, message = "채팅방 설명은 최대 200자까지 입력 가능합니다.")
    private String description;
    
    /** 채팅방이 생성된 시간 */
    private LocalDateTime createdAt;
    
    /** 채팅방 정보가 마지막으로 수정된 시간 */
    private LocalDateTime updatedAt;
    
    /** 채팅방의 마지막 메시지 ID */
    private String lastMessageId;
    
    /** 채팅방의 마지막 메시지가 전송된 시간 */
    private LocalDateTime lastMessageTime;
    
    /**
     * 채팅방 타입을 정의하는 열거형
     * DIRECT: 1:1 채팅방
     * GROUP: 여러 사용자가 참여할 수 있는 그룹 채팅방
     */
    public enum RoomType {
        DIRECT, // 1:1 채팅
        GROUP   // 그룹 채팅
    }
} 