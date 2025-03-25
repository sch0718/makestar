package com.makestar.authservice.model;

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
 * 사용자 엔티티 클래스
 * 
 * <p>시스템의 사용자 정보를 저장하는 JPA 엔티티입니다.</p>
 * 
 * <p>주요 필드:</p>
 * <ul>
 *   <li>사용자 ID (UUID)</li>
 *   <li>사용자명</li>
 *   <li>이메일</li>
 *   <li>비밀번호</li>
 *   <li>상태 정보</li>
 *   <li>마지막 접속 시간</li>
 *   <li>권한 목록</li>
 * </ul>
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 사용자 고유 식별자 (UUID)
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    /**
     * 사용자 로그인 아이디
     */
    @Column(nullable = false, unique = true)
    private String username;
    
    /**
     * 사용자 이메일 주소
     */
    @Column(nullable = false, unique = true)
    private String email;
    
    /**
     * 암호화된 비밀번호
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * 사용자 현재 상태
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.OFFLINE;
    
    /**
     * 마지막 접속 시간
     */
    @Column
    private LocalDateTime lastSeen;
    
    /**
     * 계정 생성 시간
     */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * 계정 정보 수정 시간
     */
    @Column
    private LocalDateTime updatedAt;
    
    /**
     * 사용자 권한 목록
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private Set<String> roles = new HashSet<>();
    
    /**
     * 엔티티 수정 시 자동으로 수정 시간을 업데이트
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 사용자 상태를 나타내는 열거형
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
     * 엔티티를 DTO로 변환
     * 
     * @return 사용자 정보 DTO
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
} 