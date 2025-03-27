package com.makestar.auth.controller;

import com.makestar.auth.service.AuthService;
import com.makestar.commons.dto.auth.LoginRequestDto;
import com.makestar.commons.dto.auth.LoginResponseDto;
import com.makestar.commons.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 인증 관련 REST API 컨트롤러
 * 
 * <p>사용자 인증과 관련된 엔드포인트들을 제공합니다.</p>
 * 
 * <p>제공 기능:</p>
 * <ul>
 *   <li>로그인</li>
 *   <li>회원가입</li>
 *   <li>토큰 갱신</li>
 *   <li>토큰 검증</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 사용자 로그인을 처리하는 엔드포인트
     * 
     * @param loginRequest 로그인 요청 정보 (사용자명, 비밀번호)
     * @return 로그인 응답 (액세스 토큰, 리프레시 토큰, 사용자 정보)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("로그인 요청: {}", loginRequest.getUsername());
        LoginResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 새로운 사용자 등록을 처리하는 엔드포인트
     * 
     * @param userDto 회원가입 요청 정보 (사용자명, 이메일, 비밀번호 등)
     * @return 생성된 사용자 정보
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto) {
        log.info("회원가입 요청: {}", userDto.getUsername());
        UserDto createdUser = authService.register(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 엔드포인트
     * 
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰과 사용자 정보
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestParam String refreshToken) {
        log.info("토큰 갱신 요청");
        LoginResponseDto response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * 토큰의 유효성을 검증하는 엔드포인트
     * 
     * @param token 검증할 토큰
     * @return 검증 결과 (200 OK: 유효, 401 Unauthorized: 유효하지 않음)
     */
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestParam String token) {
        log.info("토큰 검증 요청");
        if (authService.validateToken(token)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
} 