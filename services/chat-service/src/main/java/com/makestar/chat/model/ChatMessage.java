package com.makestar.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 채팅 메시지 엔티티 클래스
 * 채팅방에서 주고받는 메시지의 정보를 저장합니다.
 */
@Entity
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    /** 메시지의 고유 식별자 */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    /** 메시지가 속한 채팅방의 ID */
    @Column(nullable = false)
    private String chatRoomId;

    /** 메시지를 보낸 사용자의 ID */
    @Column(nullable = false)
    private String senderId;

    /** 메시지 내용 */
    @Column(nullable = false)
    private String content;

    /** 메시지 타입 (텍스트, 이미지, 파일, 시스템 메시지) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MessageType type = MessageType.TEXT;

    /** 메시지 전송 시간 */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

    /** 메시지 읽음 여부 */
    @Column
    @Builder.Default
    private boolean read = false;

    /** 메시지가 읽힌 시간 */
    @Column
    private LocalDateTime readAt;

    /**
     * 메시지 타입을 정의하는 열거형
     * TEXT: 일반 텍스트 메시지
     * IMAGE: 이미지 메시지
     * FILE: 파일 메시지
     * SYSTEM: 시스템 메시지 (입장, 퇴장 등)
     */
    public enum MessageType {
        TEXT, IMAGE, FILE, SYSTEM
    }

    /**
     * 메시지를 읽음 상태로 표시합니다.
     * 이미 읽은 메시지는 다시 처리하지 않습니다.
     */
    public void markAsRead() {
        if (!this.read) {
            this.read = true;
            this.readAt = LocalDateTime.now();
        }
    }
} 