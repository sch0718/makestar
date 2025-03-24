package test.java.com.makestar.userservice.repository;

import com.makestar.userservice.model.FriendRequest;
import com.makestar.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class FriendRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    private User user1;
    private User user2;
    private User user3;
    private FriendRequest friendRequest1;
    private FriendRequest friendRequest2;

    @BeforeEach
    public void setUp() {
        // 테스트 사용자 생성
        user1 = new User();
        user1.setUsername("sender");
        user1.setEmail("sender@example.com");
        user1.setStatus(User.Status.ONLINE);
        entityManager.persist(user1);

        user2 = new User();
        user2.setUsername("receiver1");
        user2.setEmail("receiver1@example.com");
        user2.setStatus(User.Status.OFFLINE);
        entityManager.persist(user2);

        user3 = new User();
        user3.setUsername("receiver2");
        user3.setEmail("receiver2@example.com");
        user3.setStatus(User.Status.AWAY);
        entityManager.persist(user3);

        // 친구 요청 생성
        friendRequest1 = new FriendRequest();
        friendRequest1.setSender(user1);
        friendRequest1.setReceiver(user2);
        friendRequest1.setStatus(FriendRequest.FriendRequestStatus.PENDING);
        entityManager.persist(friendRequest1);

        friendRequest2 = new FriendRequest();
        friendRequest2.setSender(user1);
        friendRequest2.setReceiver(user3);
        friendRequest2.setStatus(FriendRequest.FriendRequestStatus.ACCEPTED);
        entityManager.persist(friendRequest2);

        entityManager.flush();
    }

    @Test
    @DisplayName("보낸 사람과 받은 사람으로 친구 요청 존재 여부 확인")
    public void testExistsBySenderAndReceiver() {
        // When
        boolean exists1 = friendRequestRepository.existsBySenderAndReceiver(user1, user2);
        boolean exists2 = friendRequestRepository.existsBySenderAndReceiver(user2, user1);
        
        // Then
        assertThat(exists1).isTrue();
        assertThat(exists2).isFalse();
    }

    @Test
    @DisplayName("받은 사람과 상태로 친구 요청 조회")
    public void testFindByReceiverAndStatus() {
        // When
        List<FriendRequest> pendingRequests = friendRequestRepository.findByReceiverAndStatus(
                user2, FriendRequest.FriendRequestStatus.PENDING);
        
        // Then
        assertThat(pendingRequests).hasSize(1);
        assertThat(pendingRequests.get(0).getSender().getId()).isEqualTo(user1.getId());
        assertThat(pendingRequests.get(0).getReceiver().getId()).isEqualTo(user2.getId());
        assertThat(pendingRequests.get(0).getStatus()).isEqualTo(FriendRequest.FriendRequestStatus.PENDING);
    }

    @Test
    @DisplayName("보낸 사람과 상태로 친구 요청 조회")
    public void testFindBySenderAndStatus() {
        // When
        List<FriendRequest> acceptedRequests = friendRequestRepository.findBySenderAndStatus(
                user1, FriendRequest.FriendRequestStatus.ACCEPTED);
        
        // Then
        assertThat(acceptedRequests).hasSize(1);
        assertThat(acceptedRequests.get(0).getSender().getId()).isEqualTo(user1.getId());
        assertThat(acceptedRequests.get(0).getReceiver().getId()).isEqualTo(user3.getId());
        assertThat(acceptedRequests.get(0).getStatus()).isEqualTo(FriendRequest.FriendRequestStatus.ACCEPTED);
    }

    @Test
    @DisplayName("두 사용자 간의 친구 요청 조회")
    public void testFindBetweenUsers() {
        // When
        Optional<FriendRequest> request = friendRequestRepository.findBetweenUsers(user1.getId(), user2.getId());
        
        // Then
        assertThat(request).isPresent();
        assertThat(request.get().getSender().getId()).isEqualTo(user1.getId());
        assertThat(request.get().getReceiver().getId()).isEqualTo(user2.getId());
    }

    @Test
    @DisplayName("친구 요청 저장")
    public void testSaveFriendRequest() {
        // Given
        FriendRequest newRequest = new FriendRequest();
        newRequest.setSender(user2);
        newRequest.setReceiver(user3);
        newRequest.setStatus(FriendRequest.FriendRequestStatus.PENDING);
        
        // When
        FriendRequest savedRequest = friendRequestRepository.save(newRequest);
        
        // Then
        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getSender().getId()).isEqualTo(user2.getId());
        assertThat(savedRequest.getReceiver().getId()).isEqualTo(user3.getId());
        assertThat(savedRequest.getStatus()).isEqualTo(FriendRequest.FriendRequestStatus.PENDING);
    }

    @Test
    @DisplayName("친구 요청 상태 업데이트")
    public void testUpdateFriendRequestStatus() {
        // Given
        FriendRequest request = friendRequestRepository.findById(friendRequest1.getId()).get();
        request.setStatus(FriendRequest.FriendRequestStatus.REJECTED);
        
        // When
        FriendRequest updatedRequest = friendRequestRepository.save(request);
        
        // Then
        assertThat(updatedRequest.getStatus()).isEqualTo(FriendRequest.FriendRequestStatus.REJECTED);
        
        // 데이터베이스에 반영되었는지 확인
        FriendRequest foundRequest = entityManager.find(FriendRequest.class, friendRequest1.getId());
        assertThat(foundRequest.getStatus()).isEqualTo(FriendRequest.FriendRequestStatus.REJECTED);
    }

    @Test
    @DisplayName("친구 요청 삭제")
    public void testDeleteFriendRequest() {
        // When
        friendRequestRepository.delete(friendRequest1);
        
        // Then
        Optional<FriendRequest> foundRequest = friendRequestRepository.findById(friendRequest1.getId());
        assertThat(foundRequest).isEmpty();
    }
} 