package com.makestar.userservice.service;

import com.makestar.userservice.dto.FriendRequestDto;
import com.makestar.userservice.dto.UserDto;

import java.util.List;

public interface UserService {
    
    // 사용자 정보 관련 메서드
    UserDto getUserById(String userId);
    
    UserDto getUserByUsername(String username);
    
    List<UserDto> searchUsers(String keyword);
    
    UserDto updateUserProfile(String userId, UserDto userDto);
    
    UserDto updateUserStatus(String userId, String status);
    
    // 친구 관련 메서드
    List<UserDto> getFriends(String userId);
    
    FriendRequestDto sendFriendRequest(String senderId, String receiverId);
    
    FriendRequestDto acceptFriendRequest(String requestId);
    
    FriendRequestDto rejectFriendRequest(String requestId);
    
    void removeFriend(String userId, String friendId);
    
    List<FriendRequestDto> getReceivedFriendRequests(String userId);
    
    List<FriendRequestDto> getSentFriendRequests(String userId);
} 