package com.makestar.chatservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 채팅방 엔티티 클래스
 * 채팅방의 기본 정보와 참여자 목록을 관리합니다.
 */
@Entity
@Table(name = "chat_rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    /** 채팅방의 고유 식별자 */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    /** 채팅방 이름 */
    @Column(nullable = false)
    private String name;

    /** 채팅방 설명 */
    @Column
    private String description;

    /** 채팅방 생성자 ID */
    @Column(name = "creator_id")
    private String creatorId;

    /** 채팅방 타입 (1:1 채팅 또는 그룹 채팅) */
    @Column(nullable = false)
    @Builder.Default
    private ChatRoomType type = ChatRoomType.GROUP;

    /** 채팅방 참여자 ID 목록 */
    @ElementCollection
    @CollectionTable(name = "chat_room_participants", joinColumns = @JoinColumn(name = "chat_room_id"))
    @Column(name = "user_id")
    @Builder.Default
    private Set<String> participantIds = new HashSet<>();

    /** 채팅방 생성 시간 */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /** 채팅방 정보 수정 시간 */
    @Column
    private LocalDateTime updatedAt;

    /**
     * 채팅방 타입을 정의하는 열거형
     * DIRECT: 1:1 채팅방
     * GROUP: 그룹 채팅방
     */
    public enum ChatRoomType {
        DIRECT, GROUP
    }

    /**
     * 채팅방 정보가 수정될 때 자동으로 호출되어 수정 시간을 업데이트합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 채팅방에 새로운 참가자를 추가합니다.
     * @param userId 추가할 사용자 ID
     */
    public void addParticipant(String userId) {
        this.participantIds.add(userId);
    }

    /**
     * 채팅방에서 참가자를 제거합니다.
     * @param userId 제거할 사용자 ID
     */
    public void removeParticipant(String userId) {
        this.participantIds.remove(userId);
    }

    /**
     * 특정 사용자가 채팅방의 참가자인지 확인합니다.
     * @param userId 확인할 사용자 ID
     * @return 참가자인 경우 true, 아닌 경우 false
     */
    public boolean hasParticipant(String userId) {
        return this.participantIds.contains(userId);
    }
} 