package com.makestar.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.makestar.commons.model.User;
import com.makestar.auth.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security 사용자 상세 정보 서비스 구현
 * 
 * <p>데이터베이스에서 사용자 정보를 조회하여 Spring Security에서 사용할 수 있는 형식으로 변환합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자명으로 사용자 정보 조회</li>
 *   <li>사용자 권한 정보 변환</li>
 *   <li>Spring Security UserDetails 객체 생성</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자명으로 사용자 상세 정보를 조회
     * 
     * <p>데이터베이스에서 사용자 정보를 조회하고 Spring Security에서 사용할 수 있는
     * UserDetails 객체로 변환합니다.</p>
     * 
     * @param username 조회할 사용자명
     * @return Spring Security UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    // ROLE_ 접두사가 없는 경우 추가
                    String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toList());
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
} 