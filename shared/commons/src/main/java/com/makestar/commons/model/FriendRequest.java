package com.makestar.commons.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 친구 요청을 관리하는 엔티티 클래스입니다.
 * 사용자 간의 친구 관계 형성 요청을 처리하고 상태를 추적합니다.
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>친구 요청 정보 관리 (요청자, 수신자)</li>
 *   <li>요청 상태 관리 (대기중, 수락됨, 거절됨)</li>
 *   <li>시간 정보 (생성일, 수정일) 관리</li>
 * </ul>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "friend_requests")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {
    
    /** 친구 요청의 고유 식별자 */
    @Id
    private String id;
    
    /** 친구 요청을 보낸 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    /** 친구 요청을 받은 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    
    /** 친구 요청의 현재 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendRequestStatus status;
    
    /** 친구 요청이 생성된 시간 */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /** 친구 요청이 마지막으로 수정된 시간 */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 친구 요청의 상태를 나타내는 열거형입니다.
     */
    public enum FriendRequestStatus {
        /** 대기 중인 상태 */
        PENDING,
        /** 수락된 상태 */
        ACCEPTED,
        /** 거절된 상태 */
        REJECTED
    }
    
    /**
     * 엔티티가 저장되기 전에 자동으로 실행되는 메소드입니다.
     * UUID 생성, 시간 설정, 초기 상태 설정 등을 수행합니다.
     */
    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        if (this.status == null) {
            this.status = FriendRequestStatus.PENDING;
        }
    }
    
    /**
     * 엔티티가 수정되기 전에 자동으로 실행되는 메소드입니다.
     * 수정 시간을 현재 시간으로 업데이트합니다.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 