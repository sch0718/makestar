package com.makestar.userservice.model;

import com.makestar.dto.user.UserDto;
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
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.OFFLINE;
    
    @Column
    private LocalDateTime lastSeen;
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt;
    
    // 친구 관계 (다대다)
    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @Builder.Default
    private Set<User> friends = new HashSet<>();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum UserStatus {
        ONLINE, OFFLINE, AWAY, BUSY
    }
    
    // DTO 변환 메소드
    public UserDto toDto() {
        return UserDto.builder()
                .id(this.id)
                .username(this.username)
                .email(this.email)
                .status(UserDto.UserStatus.valueOf(this.status.name()))
                .lastSeen(this.lastSeen)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
    
    // 친구 추가
    public void addFriend(User friend) {
        if (friend != null && !this.equals(friend)) {
            this.friends.add(friend);
            friend.getFriends().add(this);
        }
    }
    
    // 친구 삭제
    public void removeFriend(User friend) {
        if (friend != null && this.friends.contains(friend)) {
            this.friends.remove(friend);
            friend.getFriends().remove(this);
        }
    }
} 