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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    
    private String id;
    
    @NotBlank(message = "사용자명은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "사용자명은 2자 이상 50자 이하로 입력해주세요.")
    private String username;
    
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 6, max = 100, message = "비밀번호는 6자 이상 100자 이하로 입력해주세요.")
    private String password;
    
    private UserStatus status;
    
    private LocalDateTime lastSeen;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // 비밀번호를 제외한 응답용 DTO 생성 메서드
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
    
    // 사용자 상태 열거형
    public enum UserStatus {
        ONLINE, OFFLINE, AWAY, BUSY
    }
} 