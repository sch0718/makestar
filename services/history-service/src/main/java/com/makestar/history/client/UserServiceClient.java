package com.makestar.history.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * User Service와의 통신을 담당하는 Feign 클라이언트 인터페이스입니다.
 * 사용자 정보 조회를 위해 User Service의 REST API를 호출합니다.
 * 
 * <p>이 인터페이스는 Spring Cloud Feign을 사용하여 선언적 방식으로 HTTP 클라이언트를 구현합니다.
 * User Service의 엔드포인트들을 메서드로 매핑하여 쉽게 호출할 수 있게 해줍니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 ID 기반 정보 조회</li>
 *   <li>사용자 이름 기반 정보 조회</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>
 * {@code
 * @Autowired
 * private UserServiceClient userServiceClient;
 * 
 * public void someMethod() {
 *     ResponseEntity<Map<String, Object>> userInfo = userServiceClient.getUserById("userId");
 * }
 * }
 * </pre>
 */
@FeignClient(name = "user-service")
public interface UserServiceClient {

    /**
     * 사용자 ID를 기반으로 사용자 정보를 조회합니다.
     * 
     * <p>User Service의 '/api/users/{userId}' 엔드포인트를 호출하여
     * 해당 ID를 가진 사용자의 상세 정보를 가져옵니다.</p>
     * 
     * @param userId 조회할 사용자의 ID
     * @return 사용자 정보가 담긴 ResponseEntity. 사용자 정보는 Map 형태로 반환되며,
     *         일반적으로 id, username, email 등의 기본 사용자 정보를 포함합니다.
     * @throws FeignException.NotFound 요청한 사용자 ID가 존재하지 않는 경우
     * @throws FeignException 기타 HTTP 통신 오류 발생 시
     */
    @GetMapping("/api/users/{userId}")
    ResponseEntity<Map<String, Object>> getUserById(@PathVariable("userId") String userId);

    /**
     * 사용자 이름을 기반으로 사용자 정보를 조회합니다.
     * 
     * <p>User Service의 '/api/users/username/{username}' 엔드포인트를 호출하여
     * 해당 이름을 가진 사용자의 상세 정보를 가져옵니다.</p>
     * 
     * @param username 조회할 사용자의 이름
     * @return 사용자 정보가 담긴 ResponseEntity. 사용자 정보는 Map 형태로 반환되며,
     *         일반적으로 id, username, email 등의 기본 사용자 정보를 포함합니다.
     * @throws FeignException.NotFound 요청한 사용자 이름이 존재하지 않는 경우
     * @throws FeignException 기타 HTTP 통신 오류 발생 시
     */
    @GetMapping("/api/users/username/{username}")
    ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable("username") String username);
} 