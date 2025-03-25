package com.makestar.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 로그인 요청 정보를 전달하기 위한 DTO(Data Transfer Object) 클래스입니다.
 * 사용자의 로그인 시도 시 필요한 인증 정보를 포함합니다.
 * 
 * <p>포함하는 정보:</p>
 * <ul>
 *   <li>사용자명 (username)</li>
 *   <li>비밀번호 (password)</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    
    /** 
     * 로그인할 사용자의 사용자명
     * 공백이나 null이 허용되지 않습니다.
     */
    @NotBlank(message = "사용자명은 필수 입력값입니다.")
    private String username;
    
    /**
     * 로그인할 사용자의 비밀번호
     * 공백이나 null이 허용되지 않습니다.
     */
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
} 