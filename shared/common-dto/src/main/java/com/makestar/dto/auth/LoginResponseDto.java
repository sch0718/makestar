package com.makestar.dto.auth;

import com.makestar.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 정보를 전달하기 위한 DTO(Data Transfer Object) 클래스입니다.
 * 로그인 성공 시 클라이언트에게 전달되는 인증 토큰과 사용자 정보를 포함합니다.
 * 
 * <p>포함하는 정보:</p>
 * <ul>
 *   <li>액세스 토큰 (JWT)</li>
 *   <li>리프레시 토큰</li>
 *   <li>사용자 정보</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    
    /** JWT 형식의 액세스 토큰 */
    private String token;
    
    /** 토큰 갱신을 위한 리프레시 토큰 */
    private String refreshToken;
    
    /** 로그인한 사용자의 상세 정보 */
    private UserDto user;
} 