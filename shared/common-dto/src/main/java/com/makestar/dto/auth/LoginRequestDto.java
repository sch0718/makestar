package com.makestar.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    
    @NotBlank(message = "사용자명은 필수 입력값입니다.")
    private String username;
    
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
} 