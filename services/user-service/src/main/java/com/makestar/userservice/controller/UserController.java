package com.makestar.userservice.controller;

import com.makestar.userservice.dto.FriendRequestDto;
import com.makestar.userservice.dto.UserDto;
import com.makestar.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {
        log.info("Fetching user with id: {}", userId);
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        log.info("Fetching user with username: {}", username);
        UserDto userDto = userService.getUserByUsername(username);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String keyword) {
        log.info("Searching users with keyword: {}", keyword);
        List<UserDto> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUserProfile(@PathVariable String userId, @RequestBody UserDto userDto) {
        log.info("Updating user profile with id: {}", userId);
        UserDto updatedUser = userService.updateUserProfile(userId, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<UserDto> updateUserStatus(@PathVariable String userId, @RequestParam String status) {
        log.info("Updating user status with id: {} to status: {}", userId, status);
        UserDto updatedUser = userService.updateUserStatus(userId, status);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<UserDto>> getFriends(@PathVariable String userId) {
        log.info("Fetching friends for user with id: {}", userId);
        List<UserDto> friends = userService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @PostMapping("/{userId}/friends/requests")
    public ResponseEntity<FriendRequestDto> sendFriendRequest(
            @PathVariable String userId,
            @RequestParam String receiverId) {
        log.info("Sending friend request from user: {} to user: {}", userId, receiverId);
        FriendRequestDto friendRequest = userService.sendFriendRequest(userId, receiverId);
        return new ResponseEntity<>(friendRequest, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/friends/requests/received")
    public ResponseEntity<List<FriendRequestDto>> getReceivedFriendRequests(@PathVariable String userId) {
        log.info("Fetching received friend requests for user with id: {}", userId);
        List<FriendRequestDto> friendRequests = userService.getReceivedFriendRequests(userId);
        return ResponseEntity.ok(friendRequests);
    }

    @GetMapping("/{userId}/friends/requests/sent")
    public ResponseEntity<List<FriendRequestDto>> getSentFriendRequests(@PathVariable String userId) {
        log.info("Fetching sent friend requests for user with id: {}", userId);
        List<FriendRequestDto> friendRequests = userService.getSentFriendRequests(userId);
        return ResponseEntity.ok(friendRequests);
    }

    @PutMapping("/friends/requests/{requestId}/accept")
    public ResponseEntity<FriendRequestDto> acceptFriendRequest(@PathVariable String requestId) {
        log.info("Accepting friend request with id: {}", requestId);
        FriendRequestDto friendRequest = userService.acceptFriendRequest(requestId);
        return ResponseEntity.ok(friendRequest);
    }

    @PutMapping("/friends/requests/{requestId}/reject")
    public ResponseEntity<FriendRequestDto> rejectFriendRequest(@PathVariable String requestId) {
        log.info("Rejecting friend request with id: {}", requestId);
        FriendRequestDto friendRequest = userService.rejectFriendRequest(requestId);
        return ResponseEntity.ok(friendRequest);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable String userId, @PathVariable String friendId) {
        log.info("Removing friend relationship between user: {} and friend: {}", userId, friendId);
        userService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }
} 