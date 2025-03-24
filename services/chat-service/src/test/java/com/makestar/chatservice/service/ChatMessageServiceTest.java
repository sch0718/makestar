package test.java.com.makestar.chatservice.service;

import com.makestar.chatservice.dto.ChatMessageDto;
import com.makestar.chatservice.model.ChatMessage;
import com.makestar.chatservice.repository.ChatMessageRepository;
import com.makestar.chatservice.service.impl.ChatMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    private ChatMessage message1;
    private ChatMessage message2;
    private String chatRoomId = "room1";
    private String senderId1 = "user1";
    private String senderId2 = "user2";
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.now().minusHours(1);

        message1 = ChatMessage.builder()
                .id("msg1")
                .chatRoomId(chatRoomId)
                .senderId(senderId1)
                .content("첫 번째 테스트 메시지입니다.")
                .sentAt(baseTime)
                .read(false)
                .build();

        message2 = ChatMessage.builder()
                .id("msg2")
                .chatRoomId(chatRoomId)
                .senderId(senderId2)
                .content("두 번째 테스트 메시지입니다.")
                .sentAt(baseTime.plusMinutes(30))
                .read(true)
                .readAt(baseTime.plusMinutes(35))
                .build();
    }

    @Test
    @DisplayName("채팅방 메시지 페이징 조회")
    void testGetMessagesFromChatRoom() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<ChatMessage> messages = Arrays.asList(message2, message1); // 최신 순
        Page<ChatMessage> messagePage = new PageImpl<>(messages, pageable, messages.size());
        
        when(chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(chatRoomId, pageable))
                .thenReturn(messagePage);

        // When
        Page<ChatMessageDto> result = chatMessageService.getMessagesFromChatRoom(chatRoomId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("msg2", result.getContent().get(0).getId()); // 최신 메시지가 먼저
        assertEquals("msg1", result.getContent().get(1).getId());
        verify(chatMessageRepository, times(1)).findByChatRoomIdOrderBySentAtDesc(chatRoomId, pageable);
    }

    @Test
    @DisplayName("특정 시간 이후 채팅방 메시지 조회")
    void testGetMessagesAfterTimestamp() {
        // Given
        LocalDateTime after = baseTime.plusMinutes(10);
        when(chatMessageRepository.findByChatRoomIdAndSentAtAfterOrderBySentAtAsc(chatRoomId, after))
                .thenReturn(List.of(message2));

        // When
        List<ChatMessageDto> result = chatMessageService.getMessagesAfterTimestamp(chatRoomId, after);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("msg2", result.get(0).getId());
        verify(chatMessageRepository, times(1))
                .findByChatRoomIdAndSentAtAfterOrderBySentAtAsc(chatRoomId, after);
    }

    @Test
    @DisplayName("사용자가 보낸 메시지 조회")
    void testGetMessagesBySender() {
        // Given
        when(chatMessageRepository.findBySenderId(senderId1)).thenReturn(List.of(message1));

        // When
        List<ChatMessageDto> result = chatMessageService.getMessagesBySender(senderId1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("msg1", result.get(0).getId());
        assertEquals(senderId1, result.get(0).getSenderId());
        verify(chatMessageRepository, times(1)).findBySenderId(senderId1);
    }

    @Test
    @DisplayName("채팅방의 읽지 않은 메시지 개수 조회")
    void testCountUnreadMessages() {
        // Given
        when(chatMessageRepository.countUnreadMessages(chatRoomId, senderId1)).thenReturn(1L);

        // When
        long result = chatMessageService.countUnreadMessages(chatRoomId, senderId1);

        // Then
        assertEquals(1L, result);
        verify(chatMessageRepository, times(1)).countUnreadMessages(chatRoomId, senderId1);
    }

    @Test
    @DisplayName("메시지 검색")
    void testSearchMessages() {
        // Given
        String searchTerm = "테스트";
        when(chatMessageRepository.findByContentContainingIgnoreCaseAndChatRoomId(searchTerm, chatRoomId))
                .thenReturn(Arrays.asList(message1, message2));

        // When
        List<ChatMessageDto> result = chatMessageService.searchMessages(chatRoomId, searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(chatMessageRepository, times(1))
                .findByContentContainingIgnoreCaseAndChatRoomId(searchTerm, chatRoomId);
    }

    @Test
    @DisplayName("새 메시지 저장")
    void testSaveMessage() {
        // Given
        ChatMessageDto messageDto = ChatMessageDto.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId1)
                .content("새 메시지입니다.")
                .build();

        ChatMessage savedMessage = ChatMessage.builder()
                .id("new-msg-id")
                .chatRoomId(chatRoomId)
                .senderId(senderId1)
                .content("새 메시지입니다.")
                .sentAt(LocalDateTime.now())
                .read(false)
                .build();

        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(savedMessage);

        // When
        ChatMessageDto result = chatMessageService.saveMessage(messageDto);

        // Then
        assertNotNull(result);
        assertEquals("new-msg-id", result.getId());
        assertEquals(chatRoomId, result.getChatRoomId());
        assertEquals(senderId1, result.getSenderId());
        assertEquals("새 메시지입니다.", result.getContent());
        assertFalse(result.isRead());
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("메시지 읽음 상태로 변경")
    void testMarkMessageAsRead() {
        // Given
        when(chatMessageRepository.findById("msg1")).thenReturn(Optional.of(message1));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ChatMessageDto result = chatMessageService.markMessageAsRead("msg1");

        // Then
        assertNotNull(result);
        assertEquals("msg1", result.getId());
        assertTrue(result.isRead());
        assertNotNull(result.getReadAt());
        verify(chatMessageRepository, times(1)).findById("msg1");
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("채팅방의 최근 메시지 조회")
    void testGetLatestMessages() {
        // Given
        when(chatMessageRepository.findLatestMessages(eq(chatRoomId), any(Pageable.class)))
                .thenReturn(List.of(message2));

        // When
        List<ChatMessageDto> result = chatMessageService.getLatestMessages(chatRoomId, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("msg2", result.get(0).getId());
        verify(chatMessageRepository, times(1)).findLatestMessages(eq(chatRoomId), any(Pageable.class));
    }
} 