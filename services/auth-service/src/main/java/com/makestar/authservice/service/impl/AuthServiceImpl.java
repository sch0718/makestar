package com.makestar.authservice.service.impl;

import com.makestar.authservice.model.User;
import com.makestar.authservice.repository.UserRepository;
import com.makestar.authservice.service.AuthService;
import com.makestar.dto.auth.LoginRequestDto;
import com.makestar.dto.auth.LoginResponseDto;
import com.makestar.dto.user.UserDto;
import com.makestar.utils.jwt.JwtUtils;
import com.makestar.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        // 인증 수행
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        // 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 사용자 정보 가져오기
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + loginRequest.getUsername()));
        
        // 토큰 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        
        String accessToken = jwtUtils.generateAccessToken(user.getUsername(), claims);
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());
        
        // 사용자 상태 업데이트
        user.setStatus(User.UserStatus.ONLINE);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
        
        // 응답 반환
        return LoginResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(user.toDto())
                .build();
    }

    @Override
    @Transactional
    public UserDto register(UserDto userDto) {
        // 사용자명 중복 체크
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다: " + userDto.getUsername());
        }
        
        // 이메일 중복 체크
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + userDto.getEmail());
        }
        
        // 사용자 생성
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build();
        
        // 기본 역할 부여
        user.getRoles().add("USER");
        
        // 저장
        User savedUser = userRepository.save(user);
        log.info("새 사용자가 등록되었습니다: {}", savedUser.getUsername());
        
        // DTO 반환 (비밀번호 제외)
        return savedUser.toDto();
    }

    @Override
    @Transactional
    public LoginResponseDto refreshToken(String refreshToken) {
        // 토큰 유효성 검증
        if (!jwtUtils.isTokenExpired(refreshToken)) {
            // 사용자명 추출
            String username = jwtUtils.extractUsername(refreshToken);
            
            // 사용자 정보 가져오기
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + username));
            
            // 새 액세스 토큰 생성
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", user.getRoles());
            
            String newAccessToken = jwtUtils.generateAccessToken(username, claims);
            
            // 응답 반환
            return LoginResponseDto.builder()
                    .token(newAccessToken)
                    .refreshToken(refreshToken)
                    .user(user.toDto())
                    .build();
        }
        
        throw new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다");
    }

    @Override
    public boolean validateToken(String token) {
        try {
            String username = jwtUtils.extractUsername(token);
            return !jwtUtils.isTokenExpired(token) && userRepository.existsByUsername(username);
        } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }
} 