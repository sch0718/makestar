package com.makestar.authservice.controller;

import com.makestar.authservice.service.AuthService;
import com.makestar.dto.auth.LoginRequestDto;
import com.makestar.dto.auth.LoginResponseDto;
import com.makestar.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("로그인 요청: {}", loginRequest.getUsername());
        LoginResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 회원가입 API
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto) {
        log.info("회원가입 요청: {}", userDto.getUsername());
        UserDto createdUser = authService.register(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * 토큰 갱신 API
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestParam String refreshToken) {
        log.info("토큰 갱신 요청");
        LoginResponseDto response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * 토큰 검증 API
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