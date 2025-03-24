package test.java.com.makestar.userservice.repository;

import com.makestar.userservice.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 및 조회 테스트")
    public void testSaveAndFindById() {
        // Given
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setStatus(User.Status.ONLINE);

        // When
        User savedUser = entityManager.persistAndFlush(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getStatus()).isEqualTo(User.Status.ONLINE);
    }

    @Test
    @DisplayName("사용자명으로 사용자 조회")
    public void testFindByUsername() {
        // Given
        User user = new User();
        user.setUsername("uniqueUser");
        user.setEmail("unique@example.com");
        user.setStatus(User.Status.ONLINE);
        entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByUsername("uniqueUser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("uniqueUser");
        assertThat(foundUser.get().getEmail()).isEqualTo("unique@example.com");
    }

    @Test
    @DisplayName("키워드로 사용자 검색")
    public void testSearchUsers() {
        // Given
        User user1 = new User();
        user1.setUsername("johnDoe");
        user1.setEmail("john@example.com");
        user1.setStatus(User.Status.ONLINE);
        entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setUsername("janeDoe");
        user2.setEmail("jane@example.com");
        user2.setStatus(User.Status.OFFLINE);
        entityManager.persistAndFlush(user2);

        User user3 = new User();
        user3.setUsername("bobSmith");
        user3.setEmail("bob@example.com");
        user3.setStatus(User.Status.AWAY);
        entityManager.persistAndFlush(user3);

        // When
        List<User> result = userRepository.searchUsers("doe");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getUsername)
                .containsExactlyInAnyOrder("johnDoe", "janeDoe");
    }

    @Test
    @DisplayName("친구 목록 조회")
    public void testFindFriendsByUserId() {
        // Given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setStatus(User.Status.ONLINE);
        entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setStatus(User.Status.OFFLINE);
        entityManager.persistAndFlush(user2);

        User user3 = new User();
        user3.setUsername("user3");
        user3.setEmail("user3@example.com");
        user3.setStatus(User.Status.AWAY);
        entityManager.persistAndFlush(user3);

        // 친구 관계 설정
        user1.addFriend(user2);
        user1.addFriend(user3);
        entityManager.persistAndFlush(user1);

        // When
        List<User> friends = userRepository.findFriendsByUserId(user1.getId());

        // Then
        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(User::getUsername)
                .containsExactlyInAnyOrder("user2", "user3");
    }

    @Test
    @DisplayName("이메일로 사용자 조회")
    public void testFindByEmail() {
        // Given
        User user = new User();
        user.setUsername("emailUser");
        user.setEmail("test-email@example.com");
        user.setStatus(User.Status.ONLINE);
        entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail("test-email@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("emailUser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test-email@example.com");
    }

    @Test
    @DisplayName("사용자 상태별 사용자 조회")
    public void testFindByStatus() {
        // Given
        User user1 = new User();
        user1.setUsername("onlineUser1");
        user1.setEmail("online1@example.com");
        user1.setStatus(User.Status.ONLINE);
        entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setUsername("onlineUser2");
        user2.setEmail("online2@example.com");
        user2.setStatus(User.Status.ONLINE);
        entityManager.persistAndFlush(user2);

        User user3 = new User();
        user3.setUsername("offlineUser");
        user3.setEmail("offline@example.com");
        user3.setStatus(User.Status.OFFLINE);
        entityManager.persistAndFlush(user3);

        // When
        List<User> onlineUsers = userRepository.findByStatus(User.Status.ONLINE);

        // Then
        assertThat(onlineUsers).hasSize(2);
        assertThat(onlineUsers).extracting(User::getUsername)
                .containsExactlyInAnyOrder("onlineUser1", "onlineUser2");
    }
} 