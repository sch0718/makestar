package com.makestar.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 사용자 정보를 전달하기 위한 DTO(Data Transfer Object) 클래스입니다.
 * 사용자의 기본 정보, 인증 정보, 상태 정보를 포함합니다.
 * 
 * <p>포함하는 정보:</p>
 * <ul>
 *   <li>기본 정보 (ID, 사용자명, 이메일)</li>
 *   <li>인증 정보 (비밀번호)</li>
 *   <li>상태 정보 (현재 상태, 마지막 접속 시간)</li>
 *   <li>시간 정보 (생성 시간, 수정 시간)</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    
    /** 사용자의 고유 식별자 */
    private String id;
    
    /** 
     * 사용자의 고유한 사용자명
     * 2자 이상 50자 이하로 입력해야 하며, 공백이나 null이 허용되지 않습니다.
     */
    @NotBlank(message = "사용자명은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "사용자명은 2자 이상 50자 이하로 입력해주세요.")
    private String username;
    
    /** 
     * 사용자의 이메일 주소
     * 유효한 이메일 형식이어야 합니다.
     */
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;
    
    /** 
     * 사용자의 비밀번호
     * 6자 이상 100자 이하로 입력해야 하며, 공백이나 null이 허용되지 않습니다.
     */
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 6, max = 100, message = "비밀번호는 6자 이상 100자 이하로 입력해주세요.")
    private String password;
    
    /** 
     * 사용자의 현재 상태
     * ONLINE(온라인), OFFLINE(오프라인), AWAY(자리비움), BUSY(다른 용무중) 중 하나의 값을 가집니다.
     */
    private UserStatus status;
    
    /** 사용자의 마지막 접속 시간 */
    private LocalDateTime lastSeen;
    
    /** 사용자 계정 생성 시간 */
    private LocalDateTime createdAt;
    
    /** 사용자 정보 마지막 수정 시간 */
    private LocalDateTime updatedAt;
    
    /**
     * 비밀번호를 제외한 사용자 정보를 포함하는 DTO를 생성합니다.
     * 클라이언트에 응답을 보낼 때 보안을 위해 비밀번호를 제외하고 전송하기 위해 사용됩니다.
     * 
     * @return 비밀번호가 제외된 UserDto 객체
     */
    public UserDto withoutPassword() {
        return UserDto.builder()
                .id(this.id)
                .username(this.username)
                .email(this.email)
                .status(this.status)
                .lastSeen(this.lastSeen)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
    
    /**
     * 사용자의 상태를 정의하는 열거형
     * ONLINE: 온라인 상태
     * OFFLINE: 오프라인 상태
     * AWAY: 자리비움 상태
     * BUSY: 다른 용무중 상태
     */
    public enum UserStatus {
        ONLINE, OFFLINE, AWAY, BUSY
    }
} 