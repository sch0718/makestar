package com.makestar.authservice.service;

import com.makestar.dto.auth.LoginRequestDto;
import com.makestar.dto.auth.LoginResponseDto;
import com.makestar.dto.user.UserDto;

public interface AuthService {
    
    /**
     * 사용자 로그인 처리
     * @param loginRequest 로그인 요청 정보
     * @return 로그인 응답 (토큰, 사용자 정보 등)
     */
    LoginResponseDto login(LoginRequestDto loginRequest);
    
    /**
     * 사용자 회원가입 처리
     * @param userDto 회원가입 정보
     * @return 생성된 사용자 정보
     */
    UserDto register(UserDto userDto);
    
    /**
     * 리프레시 토큰을 사용하여 액세스 토큰 갱신
     * @param refreshToken 리프레시 토큰
     * @return 로그인 응답 (새 토큰, 사용자 정보 등)
     */
    LoginResponseDto refreshToken(String refreshToken);
    
    /**
     * 토큰 유효성 검증
     * @param token 검증할 토큰
     * @return 유효 여부
     */
    boolean validateToken(String token);
} 