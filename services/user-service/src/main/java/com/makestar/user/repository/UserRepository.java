package com.makestar.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.makestar.commons.model.User;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 정보에 대한 데이터베이스 접근을 담당하는 리포지토리 인터페이스입니다.
 * JpaRepository를 상속받아 기본적인 CRUD 작업과 페이징, 정렬 기능을 제공합니다.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * 사용자명으로 사용자를 조회합니다.
     * @param username 조회할 사용자명
     * @return 조회된 사용자 정보 (Optional)
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 이메일로 사용자를 조회합니다.
     * @param email 조회할 이메일 주소
     * @return 조회된 사용자 정보 (Optional)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 해당 사용자명이 이미 존재하는지 확인합니다.
     * @param username 확인할 사용자명
     * @return 존재 여부
     */
    boolean existsByUsername(String username);
    
    /**
     * 해당 이메일이 이미 존재하는지 확인합니다.
     * @param email 확인할 이메일 주소
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
    
    /**
     * 사용자명이나 이메일에 키워드가 포함된 사용자들을 검색합니다.
     * 대소문자를 구분하지 않고 부분 일치 검색을 수행합니다.
     * 
     * @param keyword 검색할 키워드
     * @return 검색된 사용자 목록
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchUsers(@Param("keyword") String keyword);
    
    /**
     * 특정 사용자의 친구 목록을 조회합니다.
     * 양방향 관계로 설정된 friends 테이블을 조인하여 조회합니다.
     * 
     * @param userId 친구 목록을 조회할 사용자의 ID
     * @return 해당 사용자의 친구 목록
     */
    @Query("SELECT u FROM User u JOIN u.friends f WHERE f.id = :userId")
    List<User> findFriendsByUserId(@Param("userId") String userId);
} 