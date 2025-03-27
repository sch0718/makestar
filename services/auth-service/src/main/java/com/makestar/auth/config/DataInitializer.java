package com.makestar.auth.config;

import com.makestar.auth.repository.UserRepository;
import com.makestar.commons.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * 애플리케이션 시작 시 초기 데이터를 설정하는 클래스
 * 
 * <p>이 클래스는 애플리케이션이 시작될 때 필요한 초기 데이터를 생성합니다.</p>
 * <p>주요 기능으로 관리자 계정이 없을 경우 기본 관리자 계정을 생성합니다.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.admin.username:admin}")
    private String adminUsername;
    
    @Value("${app.admin.email:admin@makestar.com}")
    private String adminEmail;
    
    @Value("${app.admin.password:adminSecurePassword123!}")
    private String adminPassword;

    /**
     * 애플리케이션 시작 시 실행되는 메소드
     * 
     * @param args 명령행 인자
     */
    @Override
    @Transactional
    public void run(String... args) {
        log.info("데이터 초기화를 시작합니다...");
        try {
            initAdminUser();
            log.info("데이터 초기화가 완료되었습니다.");
        } catch (Exception e) {
            log.error("데이터 초기화 중 오류가 발생했습니다: {}", e.getMessage(), e);
        }
    }

    /**
     * 관리자 계정을 초기화하는 메소드
     * 
     * <p>관리자 계정이 존재하지 않을 경우 기본 관리자 계정을 생성합니다.</p>
     */
    private void initAdminUser() {
        // 관리자 계정이 이미 존재하는지 확인
        if (!userRepository.existsByUsername(adminUsername)) {
            log.info("관리자 계정을 생성합니다: {}", adminUsername);
            
            // 관리자 역할 설정
            Set<String> adminRoles = new HashSet<>();
            adminRoles.add("ADMIN");
            adminRoles.add("USER");
            
            // 존재하지 않는 경우, 새로운 관리자 계정 생성
            User admin = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles(adminRoles)
                    .build();
            
            // 저장
            userRepository.save(admin);
            
            log.info("관리자 계정이 성공적으로 생성되었습니다.");
        } else {
            log.info("관리자 계정이 이미 존재합니다: {}", adminUsername);
        }
    }
} 