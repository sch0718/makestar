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

/**
 * 사용자 정보를 관리하는 엔티티 클래스입니다.
 * 사용자의 기본 정보, 상태, 친구 관계 등을 포함합니다.
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 기본 정보 (ID, 사용자명, 이메일) 관리</li>
 *   <li>사용자 상태 (온라인/오프라인 등) 관리</li>
 *   <li>친구 관계 관리</li>
 *   <li>시간 정보 (생성일, 수정일, 마지막 접속일) 관리</li>
 * </ul>
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** 사용자의 고유 식별자 (UUID) */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    /** 사용자의 고유한 사용자명 */
    @Column(nullable = false, unique = true)
    private String username;
    
    /** 사용자의 고유한 이메일 주소 */
    @Column(nullable = false, unique = true)
    private String email;
    
    /** 사용자의 현재 상태 (ONLINE, OFFLINE, AWAY, BUSY) */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.OFFLINE;
    
    /** 사용자의 마지막 접속 시간 */
    @Column
    private LocalDateTime lastSeen;
    
    /** 사용자 계정 생성 시간 */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /** 사용자 정보 마지막 수정 시간 */
    @Column
    private LocalDateTime updatedAt;
    
    /**
     * 사용자의 친구 목록
     * 다대다 관계로 구현되어 있으며, user_friends 테이블을 통해 관리됩니다.
     */
    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @Builder.Default
    private Set<User> friends = new HashSet<>();
    
    /**
     * 엔티티 수정 시 자동으로 수정 시간을 업데이트합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 사용자의 상태를 나타내는 열거형입니다.
     */
    public enum UserStatus {
        /** 온라인 상태 */
        ONLINE,
        /** 오프라인 상태 */
        OFFLINE,
        /** 자리비움 상태 */
        AWAY,
        /** 다른 용무중 상태 */
        BUSY
    }
    
    /**
     * 엔티티를 DTO로 변환하는 메소드입니다.
     * 
     * @return 변환된 UserDto 객체
     */
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
    
    /**
     * 친구를 추가하는 메소드입니다.
     * 양방향 관계를 유지하기 위해 양쪽 모두에 친구 관계를 설정합니다.
     * 
     * @param friend 추가할 친구 사용자 객체
     */
    public void addFriend(User friend) {
        if (friend != null && !this.equals(friend)) {
            this.friends.add(friend);
            friend.getFriends().add(this);
        }
    }
    
    /**
     * 친구를 삭제하는 메소드입니다.
     * 양방향 관계를 유지하기 위해 양쪽 모두에서 친구 관계를 제거합니다.
     * 
     * @param friend 삭제할 친구 사용자 객체
     */
    public void removeFriend(User friend) {
        if (friend != null && this.friends.contains(friend)) {
            this.friends.remove(friend);
            friend.getFriends().remove(this);
        }
    }
} 