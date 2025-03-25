package com.makestar.historyservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 사용자 활동 이력을 저장하는 엔티티 클래스입니다.
 * 모든 사용자 활동과 관련된 정보를 추적하고 저장합니다.
 */
@Entity
@Table(name = "activity_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityHistory {

    /**
     * 활동 이력의 고유 식별자
     * UUID 형식으로 자동 생성됩니다.
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    /**
     * 활동을 수행한 사용자의 ID
     */
    @Column(nullable = false)
    private String userId;
    
    /**
     * 수행된 활동의 유형
     * @see ActivityType
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;
    
    /**
     * 활동과 관련된 엔티티의 ID (예: 게시물 ID, 댓글 ID 등)
     */
    @Column(nullable = false)
    private String entityId;
    
    /**
     * 활동과 관련된 엔티티의 유형 (예: "POST", "COMMENT" 등)
     */
    @Column
    private String entityType;
    
    /**
     * 활동에 대한 상세 설명
     */
    @Column
    private String description;
    
    /**
     * 활동이 발생한 시간
     * 기본값으로 현재 시간이 설정됩니다.
     */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * 활동이 발생한 IP 주소
     */
    @Column
    private String ipAddress;
    
    /**
     * 활동이 발생한 사용자의 브라우저/기기 정보
     */
    @Column
    private String userAgent;
    
    /**
     * 시스템에서 추적하는 모든 활동 유형을 정의하는 열거형
     */
    public enum ActivityType {
        LOGIN,              // 로그인
        LOGOUT,            // 로그아웃
        VIEW_PROFILE,      // 프로필 조회
        UPDATE_PROFILE,    // 프로필 업데이트
        CREATE_POST,       // 게시물 생성
        UPDATE_POST,       // 게시물 수정
        DELETE_POST,       // 게시물 삭제
        LIKE_POST,         // 게시물 좋아요
        CREATE_COMMENT,    // 댓글 생성
        UPDATE_COMMENT,    // 댓글 수정
        DELETE_COMMENT,    // 댓글 삭제
        SEND_MESSAGE,      // 메시지 전송
        READ_MESSAGE,      // 메시지 읽음
        CREATE_CHAT_ROOM,  // 채팅방 생성
        JOIN_CHAT_ROOM,    // 채팅방 참여
        LEAVE_CHAT_ROOM,   // 채팅방 나가기
        SEND_FRIEND_REQUEST,    // 친구 요청 전송
        ACCEPT_FRIEND_REQUEST,  // 친구 요청 수락
        REJECT_FRIEND_REQUEST,  // 친구 요청 거절
        REMOVE_FRIEND,     // 친구 삭제
        SYSTEM_ERROR,      // 시스템 오류
        OTHER             // 기타 활동
    }
} 