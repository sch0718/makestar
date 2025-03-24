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

@Entity
@Table(name = "chat_rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private ChatRoomType type = ChatRoomType.GROUP;

    @ElementCollection
    @CollectionTable(name = "chat_room_participants", joinColumns = @JoinColumn(name = "chat_room_id"))
    @Column(name = "user_id")
    @Builder.Default
    private Set<String> participantIds = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    public enum ChatRoomType {
        DIRECT, GROUP
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 참가자 추가
    public void addParticipant(String userId) {
        this.participantIds.add(userId);
    }

    // 참가자 제거
    public void removeParticipant(String userId) {
        this.participantIds.remove(userId);
    }

    // 참가자 여부 확인
    public boolean hasParticipant(String userId) {
        return this.participantIds.contains(userId);
    }
} 