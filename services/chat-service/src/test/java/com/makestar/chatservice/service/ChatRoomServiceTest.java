package test.java.com.makestar.chatservice.service;

import com.makestar.chatservice.dto.ChatRoomDto;
import com.makestar.chatservice.model.ChatRoom;
import com.makestar.chatservice.repository.ChatRoomRepository;
import com.makestar.chatservice.service.impl.ChatRoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    private ChatRoom groupChatRoom;
    private ChatRoom directChatRoom;
    private String userId1 = "user1";
    private String userId2 = "user2";
    private String userId3 = "user3";

    @BeforeEach
    void setUp() {
        // 그룹 채팅방 세팅
        groupChatRoom = ChatRoom.builder()
                .id("room1")
                .name("테스트 그룹 채팅방")
                .description("테스트용 그룹 채팅방입니다")
                .type(ChatRoom.ChatRoomType.GROUP)
                .createdAt(LocalDateTime.now())
                .build();
        groupChatRoom.addParticipant(userId1);
        groupChatRoom.addParticipant(userId2);
        groupChatRoom.addParticipant(userId3);

        // 1:1 채팅방 세팅
        directChatRoom = ChatRoom.builder()
                .id("room2")
                .name("1:1 채팅방")
                .type(ChatRoom.ChatRoomType.DIRECT)
                .createdAt(LocalDateTime.now())
                .build();
        directChatRoom.addParticipant(userId1);
        directChatRoom.addParticipant(userId2);
    }

    @Test
    @DisplayName("채팅방 ID로 채팅방 조회")
    void testGetChatRoomById() {
        // Given
        when(chatRoomRepository.findById("room1")).thenReturn(Optional.of(groupChatRoom));

        // When
        ChatRoomDto result = chatRoomService.getChatRoomById("room1");

        // Then
        assertNotNull(result);
        assertEquals("room1", result.getId());
        assertEquals("테스트 그룹 채팅방", result.getName());
        assertEquals(ChatRoom.ChatRoomType.GROUP, result.getType());
        assertEquals(3, result.getParticipantIds().size());
        assertTrue(result.getParticipantIds().contains(userId1));
        assertTrue(result.getParticipantIds().contains(userId2));
        assertTrue(result.getParticipantIds().contains(userId3));

        verify(chatRoomRepository, times(1)).findById("room1");
    }

    @Test
    @DisplayName("존재하지 않는 채팅방 ID로 조회 시 예외 발생")
    void testGetChatRoomByIdNotFound() {
        // Given
        when(chatRoomRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            chatRoomService.getChatRoomById("non-existent");
        });

        verify(chatRoomRepository, times(1)).findById("non-existent");
    }

    @Test
    @DisplayName("사용자가 참여한 채팅방 목록 조회")
    void testGetChatRoomsByUserId() {
        // Given
        List<ChatRoom> userRooms = Arrays.asList(groupChatRoom, directChatRoom);
        when(chatRoomRepository.findChatRoomsByParticipantId(userId1)).thenReturn(userRooms);

        // When
        List<ChatRoomDto> result = chatRoomService.getChatRoomsByUserId(userId1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("room1", result.get(0).getId());
        assertEquals("room2", result.get(1).getId());
        verify(chatRoomRepository, times(1)).findChatRoomsByParticipantId(userId1);
    }

    @Test
    @DisplayName("새 채팅방 생성")
    void testCreateChatRoom() {
        // Given
        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .name("새 채팅방")
                .description("새롭게 생성된 채팅방입니다")
                .type(ChatRoom.ChatRoomType.GROUP)
                .participantIds(Set.of(userId1, userId2))
                .build();

        ChatRoom newChatRoom = ChatRoom.builder()
                .id("new-room-id")
                .name("새 채팅방")
                .description("새롭게 생성된 채팅방입니다")
                .type(ChatRoom.ChatRoomType.GROUP)
                .createdAt(LocalDateTime.now())
                .build();
        newChatRoom.addParticipant(userId1);
        newChatRoom.addParticipant(userId2);

        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(newChatRoom);

        // When
        ChatRoomDto result = chatRoomService.createChatRoom(chatRoomDto);

        // Then
        assertNotNull(result);
        assertEquals("새 채팅방", result.getName());
        assertEquals("새롭게 생성된 채팅방입니다", result.getDescription());
        assertEquals(ChatRoom.ChatRoomType.GROUP, result.getType());
        assertEquals(2, result.getParticipantIds().size());
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("1:1 채팅방 찾기 또는 생성")
    void testFindOrCreateDirectChatRoom() {
        // Given
        when(chatRoomRepository.findDirectChatRoomBetweenUsers(userId1, userId2)).thenReturn(List.of(directChatRoom));

        // When
        ChatRoomDto result = chatRoomService.findOrCreateDirectChatRoom(userId1, userId2);

        // Then
        assertNotNull(result);
        assertEquals("room2", result.getId());
        assertEquals(ChatRoom.ChatRoomType.DIRECT, result.getType());
        assertEquals(2, result.getParticipantIds().size());
        verify(chatRoomRepository, times(1)).findDirectChatRoomBetweenUsers(userId1, userId2);
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("1:1 채팅방이 없을 때 새로 생성")
    void testCreateDirectChatRoomWhenNotExists() {
        // Given
        when(chatRoomRepository.findDirectChatRoomBetweenUsers(userId1, userId3)).thenReturn(Collections.emptyList());

        ChatRoom newDirectChatRoom = ChatRoom.builder()
                .id("new-direct-room")
                .name("1:1 채팅")
                .type(ChatRoom.ChatRoomType.DIRECT)
                .createdAt(LocalDateTime.now())
                .build();
        newDirectChatRoom.addParticipant(userId1);
        newDirectChatRoom.addParticipant(userId3);

        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(newDirectChatRoom);

        // When
        ChatRoomDto result = chatRoomService.findOrCreateDirectChatRoom(userId1, userId3);

        // Then
        assertNotNull(result);
        assertEquals("new-direct-room", result.getId());
        assertEquals(ChatRoom.ChatRoomType.DIRECT, result.getType());
        assertEquals(2, result.getParticipantIds().size());
        verify(chatRoomRepository, times(1)).findDirectChatRoomBetweenUsers(userId1, userId3);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방에 참가자 추가")
    void testAddParticipant() {
        // Given
        String newUserId = "user4";
        when(chatRoomRepository.findById("room1")).thenReturn(Optional.of(groupChatRoom));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(groupChatRoom);

        // When
        ChatRoomDto result = chatRoomService.addParticipant("room1", newUserId);

        // Then
        assertNotNull(result);
        assertTrue(result.getParticipantIds().contains(newUserId));
        verify(chatRoomRepository, times(1)).findById("room1");
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방에서 참가자 제거")
    void testRemoveParticipant() {
        // Given
        when(chatRoomRepository.findById("room1")).thenReturn(Optional.of(groupChatRoom));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(groupChatRoom);

        // When
        ChatRoomDto result = chatRoomService.removeParticipant("room1", userId3);

        // Then
        assertNotNull(result);
        assertFalse(result.getParticipantIds().contains(userId3));
        verify(chatRoomRepository, times(1)).findById("room1");
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 삭제")
    void testDeleteChatRoom() {
        // Given
        when(chatRoomRepository.findById("room1")).thenReturn(Optional.of(groupChatRoom));
        doNothing().when(chatRoomRepository).delete(groupChatRoom);

        // When
        chatRoomService.deleteChatRoom("room1");

        // Then
        verify(chatRoomRepository, times(1)).findById("room1");
        verify(chatRoomRepository, times(1)).delete(groupChatRoom);
    }
} 