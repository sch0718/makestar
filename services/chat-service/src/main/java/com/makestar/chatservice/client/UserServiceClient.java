package com.makestar.chatservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 사용자 서비스와의 통신을 담당하는 Feign 클라이언트 인터페이스
 * 사용자 정보 조회 등 사용자 관련 API를 호출합니다.
 */
@FeignClient(name = "user-service")
public interface UserServiceClient {

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @return 사용자 정보가 담긴 ResponseEntity. 사용자 정보는 Map 형태로 반환됩니다.
     */
    @GetMapping("/api/users/{userId}")
    ResponseEntity<Map<String, Object>> getUserById(@PathVariable("userId") String userId);

    /**
     * 사용자 이름으로 사용자 정보를 조회합니다.
     * 
     * @param username 조회할 사용자의 이름
     * @return 사용자 정보가 담긴 ResponseEntity. 사용자 정보는 Map 형태로 반환됩니다.
     */
    @GetMapping("/api/users/username/{username}")
    ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable("username") String username);

    /**
     * 여러 사용자 ID에 대한 사용자 이름 목록을 조회합니다.
     * 
     * @param userIds 조회할 사용자 ID 목록
     * @return 사용자 이름 목록이 담긴 ResponseEntity
     */
    @GetMapping("/api/users/names")
    ResponseEntity<List<String>> getUserNames(@RequestParam("userIds") List<String> userIds);
} 