package com.makestar.userservice.service;

import com.makestar.userservice.dto.FriendRequestDto;
import com.makestar.userservice.dto.UserDto;

import java.util.List;

/**
 * 사용자 서비스의 비즈니스 로직을 정의하는 인터페이스입니다.
 * 사용자 정보 관리와 친구 관계 관리에 관한 핵심 기능을 제공합니다.
 */
public interface UserService {
    
    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * @param userId 조회할 사용자의 ID
     * @return 조회된 사용자 정보 DTO
     */
    UserDto getUserById(String userId);
    
    /**
     * 사용자명으로 사용자 정보를 조회합니다.
     * @param username 조회할 사용자명
     * @return 조회된 사용자 정보 DTO
     */
    UserDto getUserByUsername(String username);
    
    /**
     * 키워드로 사용자를 검색합니다.
     * @param keyword 검색할 키워드
     * @return 검색된 사용자 목록
     */
    List<UserDto> searchUsers(String keyword);
    
    /**
     * 사용자 프로필 정보를 업데이트합니다.
     * @param userId 업데이트할 사용자의 ID
     * @param userDto 업데이트할 사용자 정보
     * @return 업데이트된 사용자 정보 DTO
     */
    UserDto updateUserProfile(String userId, UserDto userDto);
    
    /**
     * 사용자의 상태를 업데이트합니다.
     * @param userId 상태를 업데이트할 사용자의 ID
     * @param status 새로운 상태 값
     * @return 업데이트된 사용자 정보 DTO
     */
    UserDto updateUserStatus(String userId, String status);
    
    /**
     * 특정 사용자의 친구 목록을 조회합니다.
     * @param userId 친구 목록을 조회할 사용자의 ID
     * @return 친구 목록
     */
    List<UserDto> getFriends(String userId);
    
    /**
     * 친구 요청을 보냅니다.
     * @param senderId 요청을 보내는 사용자의 ID
     * @param receiverId 요청을 받는 사용자의 ID
     * @return 생성된 친구 요청 DTO
     */
    FriendRequestDto sendFriendRequest(String senderId, String receiverId);
    
    /**
     * 받은 친구 요청을 수락합니다.
     * @param requestId 수락할 친구 요청의 ID
     * @return 업데이트된 친구 요청 DTO
     */
    FriendRequestDto acceptFriendRequest(String requestId);
    
    /**
     * 받은 친구 요청을 거절합니다.
     * @param requestId 거절할 친구 요청의 ID
     * @return 업데이트된 친구 요청 DTO
     */
    FriendRequestDto rejectFriendRequest(String requestId);
    
    /**
     * 친구 관계를 삭제합니다.
     * @param userId 사용자의 ID
     * @param friendId 삭제할 친구의 ID
     */
    void removeFriend(String userId, String friendId);
    
    /**
     * 받은 친구 요청 목록을 조회합니다.
     * @param userId 요청을 받은 사용자의 ID
     * @return 받은 친구 요청 목록
     */
    List<FriendRequestDto> getReceivedFriendRequests(String userId);
    
    /**
     * 보낸 친구 요청 목록을 조회합니다.
     * @param userId 요청을 보낸 사용자의 ID
     * @return 보낸 친구 요청 목록
     */
    List<FriendRequestDto> getSentFriendRequests(String userId);
} 