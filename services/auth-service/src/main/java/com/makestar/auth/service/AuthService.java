package com.makestar.auth.service;

import com.makestar.commons.dto.auth.LoginRequestDto;
import com.makestar.commons.dto.auth.LoginResponseDto;
import com.makestar.commons.dto.user.UserDto;

/**
 * 인증 서비스 인터페이스
 * 
 * <p>사용자 인증과 관련된 핵심 기능을 정의합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 로그인</li>
 *   <li>회원가입</li>
 *   <li>토큰 갱신</li>
 *   <li>토큰 검증</li>
 * </ul>
 */
public interface AuthService {
    
    /**
     * 사용자 로그인을 처리하고 인증 토큰을 발급합니다.
     * 
     * <p>사용자가 제공한 인증 정보를 검증하고, 유효한 경우 JWT 토큰과 
     * 사용자 정보를 포함한 응답을 반환합니다.</p>
     * 
     * @param loginRequest 사용자명과 비밀번호를 포함한 로그인 요청 정보
     * @return 액세스 토큰, 리프레시 토큰, 사용자 정보를 포함한 로그인 응답
     * @throws IllegalArgumentException 잘못된 인증 정보가 제공된 경우
     */
    LoginResponseDto login(LoginRequestDto loginRequest);
    
    /**
     * 새로운 사용자 계정을 생성합니다.
     * 
     * <p>제공된 사용자 정보를 검증하고 새로운 계정을 생성합니다.
     * 비밀번호는 암호화되어 저장됩니다.</p>
     * 
     * @param userDto 사용자명, 비밀번호 등 회원가입에 필요한 정보
     * @return 생성된 사용자의 정보 (비밀번호 제외)
     * @throws IllegalArgumentException 이미 존재하는 사용자명이거나 유효하지 않은 정보인 경우
     */
    UserDto register(UserDto userDto);
    
    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.
     * 
     * <p>유효한 리프레시 토큰을 제공받아 새로운 액세스 토큰을 발급하고,
     * 필요한 경우 리프레시 토큰도 갱신합니다.</p>
     * 
     * @param refreshToken 유효한 리프레시 토큰
     * @return 새로운 액세스 토큰과 갱신된 리프레시 토큰(선택적)을 포함한 응답
     * @throws IllegalArgumentException 유효하지 않거나 만료된 리프레시 토큰인 경우
     */
    LoginResponseDto refreshToken(String refreshToken);
    
    /**
     * JWT 토큰의 유효성을 검증합니다.
     * 
     * <p>토큰의 서명, 만료 여부, 형식 등을 검증합니다.</p>
     * 
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효한 경우 true, 그렇지 않은 경우 false
     */
    boolean validateToken(String token);
} 