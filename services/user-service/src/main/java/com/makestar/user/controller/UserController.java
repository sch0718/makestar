package com.makestar.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.makestar.commons.dto.user.FriendRequestDto;
import com.makestar.commons.dto.user.UserDto;
import com.makestar.user.service.UserService;

import java.util.List;

/**
 * 사용자 관련 REST API를 제공하는 컨트롤러 클래스입니다.
 * 사용자 정보 관리와 친구 관계 관리를 위한 엔드포인트들을 정의합니다.
 * 
 * <p>기본 경로: /api/users</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 정보 조회 및 수정</li>
 *   <li>사용자 검색</li>
 *   <li>친구 관계 관리</li>
 *   <li>친구 요청 처리</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @return 사용자 정보를 포함한 ResponseEntity
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {
        log.info("Fetching user with id: {}", userId);
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    /**
     * 사용자명으로 사용자 정보를 조회합니다.
     * 
     * @param username 조회할 사용자명
     * @return 사용자 정보를 포함한 ResponseEntity
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        log.info("Fetching user with username: {}", username);
        UserDto userDto = userService.getUserByUsername(username);
        return ResponseEntity.ok(userDto);
    }

    /**
     * 키워드로 사용자를 검색합니다.
     * 
     * @param keyword 검색할 키워드
     * @return 검색된 사용자 목록을 포함한 ResponseEntity
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String keyword) {
        log.info("Searching users with keyword: {}", keyword);
        List<UserDto> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    /**
     * 사용자 프로필 정보를 업데이트합니다.
     * 
     * @param userId 업데이트할 사용자의 ID
     * @param userDto 업데이트할 사용자 정보
     * @return 업데이트된 사용자 정보를 포함한 ResponseEntity
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUserProfile(@PathVariable String userId, @RequestBody UserDto userDto) {
        log.info("Updating user profile with id: {}", userId);
        UserDto updatedUser = userService.updateUserProfile(userId, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 사용자의 상태를 업데이트합니다.
     * 
     * @param userId 상태를 업데이트할 사용자의 ID
     * @param status 새로운 상태 값
     * @return 업데이트된 사용자 정보를 포함한 ResponseEntity
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<UserDto> updateUserStatus(@PathVariable String userId, @RequestParam String status) {
        log.info("Updating user status with id: {} to status: {}", userId, status);
        UserDto updatedUser = userService.updateUserStatus(userId, status);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 특정 사용자의 친구 목록을 조회합니다.
     * 
     * @param userId 친구 목록을 조회할 사용자의 ID
     * @return 친구 목록을 포함한 ResponseEntity
     */
    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<UserDto>> getFriends(@PathVariable String userId) {
        log.info("Fetching friends for user with id: {}", userId);
        List<UserDto> friends = userService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    /**
     * 친구 요청을 보냅니다.
     * 
     * @param userId 요청을 보내는 사용자의 ID
     * @param receiverId 요청을 받는 사용자의 ID
     * @return 생성된 친구 요청 정보를 포함한 ResponseEntity
     */
    @PostMapping("/{userId}/friends/requests")
    public ResponseEntity<FriendRequestDto> sendFriendRequest(
            @PathVariable String userId,
            @RequestParam String receiverId) {
        log.info("Sending friend request from user: {} to user: {}", userId, receiverId);
        FriendRequestDto friendRequest = userService.sendFriendRequest(userId, receiverId);
        return new ResponseEntity<>(friendRequest, HttpStatus.CREATED);
    }

    /**
     * 받은 친구 요청 목록을 조회합니다.
     * 
     * @param userId 요청을 받은 사용자의 ID
     * @return 받은 친구 요청 목록을 포함한 ResponseEntity
     */
    @GetMapping("/{userId}/friends/requests/received")
    public ResponseEntity<List<FriendRequestDto>> getReceivedFriendRequests(@PathVariable String userId) {
        log.info("Fetching received friend requests for user with id: {}", userId);
        List<FriendRequestDto> friendRequests = userService.getReceivedFriendRequests(userId);
        return ResponseEntity.ok(friendRequests);
    }

    /**
     * 보낸 친구 요청 목록을 조회합니다.
     * 
     * @param userId 요청을 보낸 사용자의 ID
     * @return 보낸 친구 요청 목록을 포함한 ResponseEntity
     */
    @GetMapping("/{userId}/friends/requests/sent")
    public ResponseEntity<List<FriendRequestDto>> getSentFriendRequests(@PathVariable String userId) {
        log.info("Fetching sent friend requests for user with id: {}", userId);
        List<FriendRequestDto> friendRequests = userService.getSentFriendRequests(userId);
        return ResponseEntity.ok(friendRequests);
    }

    /**
     * 받은 친구 요청을 수락합니다.
     * 
     * @param requestId 수락할 친구 요청의 ID
     * @return 업데이트된 친구 요청 정보를 포함한 ResponseEntity
     */
    @PutMapping("/friends/requests/{requestId}/accept")
    public ResponseEntity<FriendRequestDto> acceptFriendRequest(@PathVariable String requestId) {
        log.info("Accepting friend request with id: {}", requestId);
        FriendRequestDto friendRequest = userService.acceptFriendRequest(requestId);
        return ResponseEntity.ok(friendRequest);
    }

    /**
     * 받은 친구 요청을 거절합니다.
     * 
     * @param requestId 거절할 친구 요청의 ID
     * @return 업데이트된 친구 요청 정보를 포함한 ResponseEntity
     */
    @PutMapping("/friends/requests/{requestId}/reject")
    public ResponseEntity<FriendRequestDto> rejectFriendRequest(@PathVariable String requestId) {
        log.info("Rejecting friend request with id: {}", requestId);
        FriendRequestDto friendRequest = userService.rejectFriendRequest(requestId);
        return ResponseEntity.ok(friendRequest);
    }

    /**
     * 친구 관계를 삭제합니다.
     * 
     * @param userId 사용자의 ID
     * @param friendId 삭제할 친구의 ID
     * @return 빈 ResponseEntity (204 No Content)
     */
    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable String userId, @PathVariable String friendId) {
        log.info("Removing friend relationship between user: {} and friend: {}", userId, friendId);
        userService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }
} 