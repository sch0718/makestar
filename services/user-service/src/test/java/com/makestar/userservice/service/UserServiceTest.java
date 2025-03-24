package test.java.com.makestar.userservice.service;

import com.makestar.userservice.dto.FriendRequestDto;
import com.makestar.userservice.dto.UserDto;
import com.makestar.userservice.model.FriendRequest;
import com.makestar.userservice.model.User;
import com.makestar.userservice.repository.FriendRequestRepository;
import com.makestar.userservice.repository.UserRepository;
import com.makestar.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private FriendRequest friendRequest;

    @BeforeEach
    void setUp() {
        // 테스트용 User 객체 생성
        user1 = new User();
        user1.setId("1");
        user1.setUsername("testUser");
        user1.setEmail("test@example.com");
        user1.setStatus(User.Status.ONLINE);

        user2 = new User();
        user2.setId("2");
        user2.setUsername("anotherUser");
        user2.setEmail("another@example.com");
        user2.setStatus(User.Status.OFFLINE);

        // 테스트용 FriendRequest 생성
        friendRequest = new FriendRequest();
        friendRequest.setId("1");
        friendRequest.setSender(user1);
        friendRequest.setReceiver(user2);
        friendRequest.setStatus(FriendRequest.FriendRequestStatus.PENDING);
        friendRequest.setCreatedAt(LocalDateTime.now());
        friendRequest.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("사용자 ID로 사용자 조회")
    void testGetUserById() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.of(user1));

        // When
        UserDto result = userService.getUserById("1");

        // Then
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("ONLINE", result.getStatus());

        verify(userRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 조회 시 예외 발생")
    void testGetUserByIdNotFound() {
        // Given
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById("999"));
        verify(userRepository, times(1)).findById("999");
    }

    @Test
    @DisplayName("사용자명으로 사용자 조회")
    void testGetUserByUsername() {
        // Given
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user1));

        // When
        UserDto result = userService.getUserByUsername("testUser");

        // Then
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    @DisplayName("키워드로 사용자 검색")
    void testSearchUsers() {
        // Given
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.searchUsers("user")).thenReturn(users);

        // When
        List<UserDto> result = userService.searchUsers("user");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testUser", result.get(0).getUsername());
        assertEquals("anotherUser", result.get(1).getUsername());
        verify(userRepository, times(1)).searchUsers("user");
    }

    @Test
    @DisplayName("사용자 프로필 업데이트")
    void testUpdateUserProfile() {
        // Given
        UserDto updateDto = UserDto.builder()
                .id("1")
                .username("updatedUser")
                .email("updated@example.com")
                .build();

        User updatedUser = new User();
        updatedUser.setId("1");
        updatedUser.setUsername("updatedUser");
        updatedUser.setEmail("updated@example.com");

        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserDto result = userService.updateUserProfile("1", updateDto);

        // Then
        assertNotNull(result);
        assertEquals("updatedUser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 상태 업데이트")
    void testUpdateUserStatus() {
        // Given
        User updatedUser = new User();
        updatedUser.setId("1");
        updatedUser.setUsername("testUser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setStatus(User.Status.AWAY);

        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserDto result = userService.updateUserStatus("1", "AWAY");

        // Then
        assertNotNull(result);
        assertEquals("AWAY", result.getStatus());
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("사용자의 친구 목록 조회")
    void testGetFriends() {
        // Given
        List<User> friends = Arrays.asList(user2);
        when(userRepository.findFriendsByUserId("1")).thenReturn(friends);

        // When
        List<UserDto> result = userService.getFriends("1");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("anotherUser", result.get(0).getUsername());
        verify(userRepository, times(1)).findFriendsByUserId("1");
    }

    @Test
    @DisplayName("친구 요청 보내기")
    void testSendFriendRequest() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        when(userRepository.findById("2")).thenReturn(Optional.of(user2));
        when(friendRequestRepository.existsBySenderAndReceiver(user1, user2)).thenReturn(false);
        when(friendRequestRepository.save(any(FriendRequest.class))).thenReturn(friendRequest);

        // When
        FriendRequestDto result = userService.sendFriendRequest("1", "2");

        // Then
        assertNotNull(result);
        assertEquals("1", result.getSenderId());
        assertEquals("2", result.getReceiverId());
        assertEquals("PENDING", result.getStatus());
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).findById("2");
        verify(friendRequestRepository, times(1)).existsBySenderAndReceiver(user1, user2);
        verify(friendRequestRepository, times(1)).save(any(FriendRequest.class));
    }

    @Test
    @DisplayName("자기 자신에게 친구 요청 보낼 수 없음")
    void testSendFriendRequestToSelf() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.sendFriendRequest("1", "1"));
    }

    @Test
    @DisplayName("친구 요청 수락")
    void testAcceptFriendRequest() {
        // Given
        when(friendRequestRepository.findById("1")).thenReturn(Optional.of(friendRequest));
        when(userRepository.save(any(User.class))).thenReturn(user1).thenReturn(user2);
        when(friendRequestRepository.save(any(FriendRequest.class))).thenReturn(friendRequest);

        // 상태 변경
        FriendRequest acceptedRequest = friendRequest;
        acceptedRequest.setStatus(FriendRequest.FriendRequestStatus.ACCEPTED);

        // When
        FriendRequestDto result = userService.acceptFriendRequest("1");

        // Then
        assertNotNull(result);
        assertEquals("ACCEPTED", result.getStatus());
        verify(friendRequestRepository, times(1)).findById("1");
        verify(userRepository, times(2)).save(any(User.class));
        verify(friendRequestRepository, times(1)).save(any(FriendRequest.class));
    }

    @Test
    @DisplayName("친구 요청 거절")
    void testRejectFriendRequest() {
        // Given
        when(friendRequestRepository.findById("1")).thenReturn(Optional.of(friendRequest));

        // 상태 변경
        FriendRequest rejectedRequest = friendRequest;
        rejectedRequest.setStatus(FriendRequest.FriendRequestStatus.REJECTED);
        when(friendRequestRepository.save(any(FriendRequest.class))).thenReturn(rejectedRequest);

        // When
        FriendRequestDto result = userService.rejectFriendRequest("1");

        // Then
        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        verify(friendRequestRepository, times(1)).findById("1");
        verify(friendRequestRepository, times(1)).save(any(FriendRequest.class));
    }

    @Test
    @DisplayName("받은 친구 요청 조회")
    void testGetReceivedFriendRequests() {
        // Given
        List<FriendRequest> requests = Arrays.asList(friendRequest);
        when(userRepository.findById("2")).thenReturn(Optional.of(user2));
        when(friendRequestRepository.findByReceiverAndStatus(
                eq(user2), eq(FriendRequest.FriendRequestStatus.PENDING)))
                .thenReturn(requests);

        // When
        List<FriendRequestDto> result = userService.getReceivedFriendRequests("2");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getSenderId());
        assertEquals("2", result.get(0).getReceiverId());
        verify(userRepository, times(1)).findById("2");
        verify(friendRequestRepository, times(1)).findByReceiverAndStatus(
                eq(user2), eq(FriendRequest.FriendRequestStatus.PENDING));
    }

    @Test
    @DisplayName("보낸 친구 요청 조회")
    void testGetSentFriendRequests() {
        // Given
        List<FriendRequest> requests = Arrays.asList(friendRequest);
        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        when(friendRequestRepository.findBySenderAndStatus(
                eq(user1), eq(FriendRequest.FriendRequestStatus.PENDING)))
                .thenReturn(requests);

        // When
        List<FriendRequestDto> result = userService.getSentFriendRequests("1");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getSenderId());
        assertEquals("2", result.get(0).getReceiverId());
        verify(userRepository, times(1)).findById("1");
        verify(friendRequestRepository, times(1)).findBySenderAndStatus(
                eq(user1), eq(FriendRequest.FriendRequestStatus.PENDING));
    }

    @Test
    @DisplayName("친구 관계 삭제")
    void testRemoveFriend() {
        // Given
        user1.addFriend(user2);
        user2.addFriend(user1);

        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        when(userRepository.findById("2")).thenReturn(Optional.of(user2));
        when(friendRequestRepository.findBetweenUsers("1", "2")).thenReturn(Optional.of(friendRequest));

        // When
        userService.removeFriend("1", "2");

        // Then
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).findById("2");
        verify(userRepository, times(1)).save(user1);
        verify(userRepository, times(1)).save(user2);
        verify(friendRequestRepository, times(1)).findBetweenUsers("1", "2");
        verify(friendRequestRepository, times(1)).delete(friendRequest);
    }
} 