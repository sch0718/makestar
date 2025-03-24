package test.java.com.makestar.chatservice.repository;

import com.makestar.chatservice.model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ChatMessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private String chatRoomId1 = "room1";
    private String chatRoomId2 = "room2";
    private String senderId1 = "user1";
    private String senderId2 = "user2";
    private ChatMessage message1;
    private ChatMessage message2;
    private ChatMessage message3;
    private ChatMessage message4;
    private LocalDateTime baseTime;

    @BeforeEach
    public void setUp() {
        baseTime = LocalDateTime.now().minusHours(1);
        
        // 첫 번째 채팅방의 메시지들
        message1 = ChatMessage.builder()
                .chatRoomId(chatRoomId1)
                .senderId(senderId1)
                .content("첫 번째 메시지입니다.")
                .type(ChatMessage.MessageType.TEXT)
                .sentAt(baseTime)
                .read(false)
                .build();
        entityManager.persist(message1);

        message2 = ChatMessage.builder()
                .chatRoomId(chatRoomId1)
                .senderId(senderId2)
                .content("두 번째 메시지입니다.")
                .type(ChatMessage.MessageType.TEXT)
                .sentAt(baseTime.plusMinutes(30))
                .read(true)
                .readAt(baseTime.plusMinutes(35))
                .build();
        entityManager.persist(message2);

        // 두 번째 채팅방의 메시지들
        message3 = ChatMessage.builder()
                .chatRoomId(chatRoomId2)
                .senderId(senderId1)
                .content("다른 채팅방의 메시지입니다.")
                .type(ChatMessage.MessageType.TEXT)
                .sentAt(baseTime.plusMinutes(10))
                .read(false)
                .build();
        entityManager.persist(message3);

        message4 = ChatMessage.builder()
                .chatRoomId(chatRoomId2)
                .senderId(senderId2)
                .content("이미지 메시지입니다.")
                .type(ChatMessage.MessageType.IMAGE)
                .sentAt(baseTime.plusMinutes(40))
                .read(false)
                .build();
        entityManager.persist(message4);

        entityManager.flush();
    }

    @Test
    @DisplayName("채팅방 메시지 페이징 조회 테스트")
    public void testFindByChatRoomIdOrderBySentAtDesc() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ChatMessage> messagesPage = chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(chatRoomId1, pageable);

        // Then
        assertThat(messagesPage.getContent()).hasSize(2);
        assertThat(messagesPage.getContent().get(0).getId()).isEqualTo(message2.getId());
        assertThat(messagesPage.getContent().get(1).getId()).isEqualTo(message1.getId());
    }

    @Test
    @DisplayName("특정 시간 이후 채팅방 메시지 조회 테스트")
    public void testFindByChatRoomIdAndSentAtAfterOrderBySentAtAsc() {
        // Given
        LocalDateTime afterTime = baseTime.plusMinutes(20);

        // When
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdAndSentAtAfterOrderBySentAtAsc(chatRoomId1, afterTime);

        // Then
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getId()).isEqualTo(message2.getId());
    }

    @Test
    @DisplayName("사용자가 보낸 메시지 조회 테스트")
    public void testFindBySenderId() {
        // When
        List<ChatMessage> user1Messages = chatMessageRepository.findBySenderId(senderId1);
        List<ChatMessage> user2Messages = chatMessageRepository.findBySenderId(senderId2);

        // Then
        assertThat(user1Messages).hasSize(2);
        assertThat(user1Messages).extracting(ChatMessage::getId)
                .containsExactlyInAnyOrder(message1.getId(), message3.getId());
        
        assertThat(user2Messages).hasSize(2);
        assertThat(user2Messages).extracting(ChatMessage::getId)
                .containsExactlyInAnyOrder(message2.getId(), message4.getId());
    }

    @Test
    @DisplayName("채팅방의 읽지 않은 메시지 개수 조회 테스트")
    public void testCountUnreadMessages() {
        // When
        long unreadCountForUser1 = chatMessageRepository.countUnreadMessages(chatRoomId1, senderId1);
        long unreadCountForUser2 = chatMessageRepository.countUnreadMessages(chatRoomId1, senderId2);

        // Then
        assertThat(unreadCountForUser1).isEqualTo(0); // user1이 보낸 메시지는 자신의 것이므로 0개
        assertThat(unreadCountForUser2).isEqualTo(1); // user1이 보낸 message1은 읽지 않음
    }

    @Test
    @DisplayName("채팅방의 최근 메시지 조회 테스트")
    public void testFindLatestMessages() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);

        // When
        List<ChatMessage> latestMessages = chatMessageRepository.findLatestMessages(chatRoomId1, pageable);

        // Then
        assertThat(latestMessages).hasSize(1);
        assertThat(latestMessages.get(0).getId()).isEqualTo(message2.getId());
    }

    @Test
    @DisplayName("메시지 내용으로 검색 테스트")
    public void testFindByContentContainingIgnoreCaseAndChatRoomId() {
        // When
        List<ChatMessage> messagesWithFirst = chatMessageRepository.findByContentContainingIgnoreCaseAndChatRoomId("첫", chatRoomId1);
        List<ChatMessage> messagesWithMessage = chatMessageRepository.findByContentContainingIgnoreCaseAndChatRoomId("메시지", chatRoomId1);
        List<ChatMessage> messagesWithNonExisting = chatMessageRepository.findByContentContainingIgnoreCaseAndChatRoomId("존재하지않음", chatRoomId1);

        // Then
        assertThat(messagesWithFirst).hasSize(1);
        assertThat(messagesWithFirst.get(0).getId()).isEqualTo(message1.getId());
        
        assertThat(messagesWithMessage).hasSize(2);
        assertThat(messagesWithMessage).extracting(ChatMessage::getId)
                .containsExactlyInAnyOrder(message1.getId(), message2.getId());
        
        assertThat(messagesWithNonExisting).isEmpty();
    }

    @Test
    @DisplayName("메시지 저장 테스트")
    public void testSaveMessage() {
        // Given
        ChatMessage newMessage = ChatMessage.builder()
                .chatRoomId(chatRoomId1)
                .senderId(senderId1)
                .content("새로운 메시지입니다.")
                .type(ChatMessage.MessageType.TEXT)
                .sentAt(LocalDateTime.now())
                .build();

        // When
        ChatMessage savedMessage = chatMessageRepository.save(newMessage);

        // Then
        assertThat(savedMessage.getId()).isNotNull();
        assertThat(savedMessage.getContent()).isEqualTo("새로운 메시지입니다.");
        assertThat(savedMessage.getChatRoomId()).isEqualTo(chatRoomId1);
        assertThat(savedMessage.getSenderId()).isEqualTo(senderId1);
    }

    @Test
    @DisplayName("메시지 읽음 상태 변경 테스트")
    public void testMarkMessageAsRead() {
        // Given
        ChatMessage message = chatMessageRepository.findById(message1.getId()).get();
        assertThat(message.isRead()).isFalse();

        // When
        message.markAsRead();
        chatMessageRepository.save(message);

        // Then
        ChatMessage updatedMessage = chatMessageRepository.findById(message1.getId()).get();
        assertThat(updatedMessage.isRead()).isTrue();
        assertThat(updatedMessage.getReadAt()).isNotNull();
    }
} 