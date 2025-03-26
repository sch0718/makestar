package com.makestar.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.makestar.commons.model.User;

import java.util.Optional;

/**
 * 사용자 정보 리포지토리
 * 
 * <p>사용자 엔티티에 대한 데이터베이스 작업을 처리하는 JPA 리포지토리입니다.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * 사용자명으로 사용자 정보를 조회
     * 
     * @param username 조회할 사용자명
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 이메일로 사용자 정보를 조회
     * 
     * @param email 조회할 이메일 주소
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 사용자명 존재 여부를 확인
     * 
     * @param username 확인할 사용자명
     * @return 존재 여부
     */
    boolean existsByUsername(String username);
    
    /**
     * 이메일 주소 존재 여부를 확인
     * 
     * @param email 확인할 이메일 주소
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
} 