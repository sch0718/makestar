package com.makestar.userservice.service.impl;

import com.makestar.userservice.dto.FriendRequestDto;
import com.makestar.userservice.dto.UserDto;
import com.makestar.userservice.model.FriendRequest;
import com.makestar.userservice.model.User;
import com.makestar.userservice.repository.FriendRequestRepository;
import com.makestar.userservice.repository.UserRepository;
import com.makestar.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return UserDto.fromEntity(user);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        return UserDto.fromEntity(user);
    }

    @Override
    public List<UserDto> searchUsers(String keyword) {
        List<User> users = userRepository.searchUsers(keyword);
        return users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUserProfile(String userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        
        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUserStatus(String userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        user.setStatus(User.UserStatus.valueOf(status));
        user.setLastSeen(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    @Override
    public List<UserDto> getFriends(String userId) {
        List<User> friends = userRepository.findFriendsByUserId(userId);
        return friends.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FriendRequestDto sendFriendRequest(String senderId, String receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found with id: " + senderId));
        
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found with id: " + receiverId));
        
        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new IllegalStateException("Friend request already exists");
        }
        
        if (sender.getFriends().contains(receiver)) {
            throw new IllegalStateException("Already friends");
        }
        
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequest.FriendRequestStatus.PENDING)
                .build();
        
        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);
        
        return convertToDto(savedFriendRequest);
    }

    @Override
    @Transactional
    public FriendRequestDto acceptFriendRequest(String requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found with id: " + requestId));
        
        if (friendRequest.getStatus() != FriendRequest.FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Friend request is not pending");
        }
        
        User sender = friendRequest.getSender();
        User receiver = friendRequest.getReceiver();
        
        sender.addFriend(receiver);
        receiver.addFriend(sender);
        
        friendRequest.setStatus(FriendRequest.FriendRequestStatus.ACCEPTED);
        
        userRepository.save(sender);
        userRepository.save(receiver);
        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);
        
        return convertToDto(savedFriendRequest);
    }

    @Override
    @Transactional
    public FriendRequestDto rejectFriendRequest(String requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found with id: " + requestId));
        
        if (friendRequest.getStatus() != FriendRequest.FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Friend request is not pending");
        }
        
        friendRequest.setStatus(FriendRequest.FriendRequestStatus.REJECTED);
        
        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);
        
        return convertToDto(savedFriendRequest);
    }

    @Override
    @Transactional
    public void removeFriend(String userId, String friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend not found with id: " + friendId));
        
        if (!user.getFriends().contains(friend)) {
            throw new IllegalStateException("Not friends");
        }
        
        user.removeFriend(friend);
        friend.removeFriend(user);
        
        userRepository.save(user);
        userRepository.save(friend);
        
        // 기존 친구 요청 기록 찾아서 제거
        friendRequestRepository.findBetweenUsers(userId, friendId)
                .ifPresent(friendRequestRepository::delete);
    }

    @Override
    public List<FriendRequestDto> getReceivedFriendRequests(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        List<FriendRequest> friendRequests = friendRequestRepository.findByReceiverAndStatus(
                user, FriendRequest.FriendRequestStatus.PENDING);
        
        return friendRequests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequestDto> getSentFriendRequests(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        List<FriendRequest> friendRequests = friendRequestRepository.findBySenderAndStatus(
                user, FriendRequest.FriendRequestStatus.PENDING);
        
        return friendRequests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private FriendRequestDto convertToDto(FriendRequest friendRequest) {
        return FriendRequestDto.builder()
                .id(friendRequest.getId())
                .senderId(friendRequest.getSender().getId())
                .receiverId(friendRequest.getReceiver().getId())
                .status(friendRequest.getStatus().name())
                .createdAt(friendRequest.getCreatedAt())
                .updatedAt(friendRequest.getUpdatedAt())
                .build();
    }
} 