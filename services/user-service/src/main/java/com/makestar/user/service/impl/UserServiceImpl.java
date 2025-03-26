package com.makestar.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.makestar.commons.dto.user.FriendRequestDto;
import com.makestar.commons.dto.user.UserDto;
import com.makestar.commons.model.FriendRequest;
import com.makestar.commons.model.User;
import com.makestar.user.repository.FriendRequestRepository;
import com.makestar.user.repository.UserRepository;
import com.makestar.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService 인터페이스의 구현체입니다.
 * 사용자 정보 관리와 친구 관계 관리에 관한 비즈니스 로직을 구현합니다.
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 정보 조회, 수정, 검색</li>
 *   <li>사용자 상태 관리</li>
 *   <li>친구 관계 관리</li>
 *   <li>친구 요청 처리</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @return 조회된 사용자 정보 DTO
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return UserDto.fromEntity(user);
    }

    /**
     * 사용자명으로 사용자 정보를 조회합니다.
     * 
     * @param username 조회할 사용자명
     * @return 조회된 사용자 정보 DTO
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        return UserDto.fromEntity(user);
    }

    /**
     * 키워드로 사용자를 검색합니다.
     * 
     * @param keyword 검색할 키워드
     * @return 검색된 사용자 목록
     */
    @Override
    public List<UserDto> searchUsers(String keyword) {
        List<User> users = userRepository.searchUsers(keyword);
        return users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 프로필 정보를 업데이트합니다.
     * 
     * @param userId 업데이트할 사용자의 ID
     * @param userDto 업데이트할 사용자 정보
     * @return 업데이트된 사용자 정보 DTO
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우
     */
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

    /**
     * 사용자의 상태를 업데이트합니다.
     * 상태 변경 시 마지막 접속 시간도 함께 업데이트됩니다.
     * 
     * @param userId 상태를 업데이트할 사용자의 ID
     * @param status 새로운 상태 값
     * @return 업데이트된 사용자 정보 DTO
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우
     */
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

    /**
     * 특정 사용자의 친구 목록을 조회합니다.
     * 
     * @param userId 친구 목록을 조회할 사용자의 ID
     * @return 친구 목록
     */
    @Override
    public List<UserDto> getFriends(String userId) {
        List<User> friends = userRepository.findFriendsByUserId(userId);
        return friends.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 친구 요청을 보냅니다.
     * 자기 자신에게 요청을 보내거나, 이미 친구인 경우, 이미 요청이 존재하는 경우에는 예외가 발생합니다.
     * 
     * @param senderId 요청을 보내는 사용자의 ID
     * @param receiverId 요청을 받는 사용자의 ID
     * @return 생성된 친구 요청 DTO
     * @throws IllegalArgumentException 자기 자신에게 요청을 보내는 경우
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우
     * @throws IllegalStateException 이미 친구이거나 요청이 존재하는 경우
     */
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

    /**
     * 받은 친구 요청을 수락합니다.
     * 요청이 수락되면 양쪽 모두의 친구 목록에 서로가 추가됩니다.
     * 
     * @param requestId 수락할 친구 요청의 ID
     * @return 업데이트된 친구 요청 DTO
     * @throws EntityNotFoundException 친구 요청을 찾을 수 없는 경우
     * @throws IllegalStateException 요청이 대기 상태가 아닌 경우
     */
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

    /**
     * 받은 친구 요청을 거절합니다.
     * 
     * @param requestId 거절할 친구 요청의 ID
     * @return 업데이트된 친구 요청 DTO
     * @throws EntityNotFoundException 친구 요청을 찾을 수 없는 경우
     * @throws IllegalStateException 요청이 대기 상태가 아닌 경우
     */
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

    /**
     * 친구 관계를 삭제합니다.
     * 양쪽 모두의 친구 목록에서 서로가 제거되며, 관련된 친구 요청 기록도 삭제됩니다.
     * 
     * @param userId 사용자의 ID
     * @param friendId 삭제할 친구의 ID
     * @throws EntityNotFoundException 사용자나 친구를 찾을 수 없는 경우
     * @throws IllegalStateException 친구 관계가 아닌 경우
     */
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

    /**
     * 받은 친구 요청 목록을 조회합니다.
     * 대기 중인 요청만 조회됩니다.
     * 
     * @param userId 요청을 받은 사용자의 ID
     * @return 받은 친구 요청 목록
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우
     */
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

    /**
     * 보낸 친구 요청 목록을 조회합니다.
     * 대기 중인 요청만 조회됩니다.
     * 
     * @param userId 요청을 보낸 사용자의 ID
     * @return 보낸 친구 요청 목록
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우
     */
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
    
    /**
     * FriendRequest 엔티티를 DTO로 변환합니다.
     * 
     * @param friendRequest 변환할 친구 요청 엔티티
     * @return 변환된 친구 요청 DTO
     */
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