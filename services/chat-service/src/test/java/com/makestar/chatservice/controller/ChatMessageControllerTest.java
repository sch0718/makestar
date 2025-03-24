package test.java.com.makestar.chatservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makestar.chatservice.dto.ChatMessageDto;
import com.makestar.chatservice.service.ChatMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatMessageController.class)
public class ChatMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatMessageService chatMessageService;

    @Autowired
    private ObjectMapper objectMapper;

    private ChatMessageDto message1;
    private ChatMessageDto message2;
    private String chatRoomId = "room1";
    private String senderId1 = "user1";
    private String senderId2 = "user2";
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        message1 = ChatMessageDto.builder()
                .id("msg1")
                .chatRoomId(chatRoomId)
                .senderId(senderId1)
                .senderName("사용자1")
                .content("첫 번째 테스트 메시지입니다.")
                .sentAt(now.minusMinutes(5))
                .read(false)
                .build();
                
        message2 = ChatMessageDto.builder()
                .id("msg2")
                .chatRoomId(chatRoomId)
                .senderId(senderId2)
                .senderName("사용자2")
                .content("두 번째 테스트 메시지입니다.")
                .sentAt(now)
                .read(true)
                .readAt(now.plusMinutes(1))
                .build();
    }

    @Test
    @DisplayName("채팅방 메시지 페이지 조회")
    void testGetMessagesFromChatRoom() throws Exception {
        // Given
        List<ChatMessageDto> messages = Arrays.asList(message2, message1);
        Page<ChatMessageDto> messagePage = new PageImpl<>(messages, PageRequest.of(0, 10), messages.size());
        
        when(chatMessageService.getMessagesFromChatRoom(eq(chatRoomId), any(Pageable.class)))
                .thenReturn(messagePage);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/messages/" + chatRoomId)
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is("msg2")))
                .andExpect(jsonPath("$.content[1].id", is("msg1")));
    }

    @Test
    @DisplayName("특정 시간 이후의 메시지 조회")
    void testGetMessagesAfterTimestamp() throws Exception {
        // Given
        LocalDateTime timestamp = now.minusMinutes(1);
        when(chatMessageService.getMessagesAfterTimestamp(eq(chatRoomId), any(LocalDateTime.class)))
                .thenReturn(List.of(message2));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/messages/" + chatRoomId + "/after")
                .contentType(MediaType.APPLICATION_JSON)
                .param("timestamp", timestamp.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("msg2")));
    }

    @Test
    @DisplayName("사용자가 보낸 메시지 조회")
    void testGetMessagesBySender() throws Exception {
        // Given
        when(chatMessageService.getMessagesBySender(senderId1))
                .thenReturn(List.of(message1));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/messages/sender/" + senderId1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("msg1")))
                .andExpect(jsonPath("$[0].senderId", is(senderId1)));
    }

    @Test
    @DisplayName("읽지 않은 메시지 수 조회")
    void testCountUnreadMessages() throws Exception {
        // Given
        when(chatMessageService.countUnreadMessages(chatRoomId, senderId2))
                .thenReturn(1L);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/messages/" + chatRoomId + "/unread")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", senderId2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }

    @Test
    @DisplayName("메시지 검색")
    void testSearchMessages() throws Exception {
        // Given
        String searchTerm = "테스트";
        when(chatMessageService.searchMessages(chatRoomId, searchTerm))
                .thenReturn(Arrays.asList(message1, message2));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/messages/" + chatRoomId + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("term", searchTerm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("새 메시지 전송")
    void testSendMessage() throws Exception {
        // Given
        ChatMessageDto newMessageRequest = ChatMessageDto.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId1)
                .senderName("사용자1")
                .content("새 메시지입니다.")
                .build();
        
        ChatMessageDto savedMessage = ChatMessageDto.builder()
                .id("new-msg-id")
                .chatRoomId(chatRoomId)
                .senderId(senderId1)
                .senderName("사용자1")
                .content("새 메시지입니다.")
                .sentAt(now)
                .read(false)
                .build();
        
        when(chatMessageService.saveMessage(any(ChatMessageDto.class))).thenReturn(savedMessage);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMessageRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("new-msg-id")))
                .andExpect(jsonPath("$.chatRoomId", is(chatRoomId)))
                .andExpect(jsonPath("$.senderId", is(senderId1)))
                .andExpect(jsonPath("$.content", is("새 메시지입니다.")));
    }

    @Test
    @DisplayName("메시지 읽음 상태로 표시")
    void testMarkMessageAsRead() throws Exception {
        // Given
        ChatMessageDto readMessage = ChatMessageDto.builder()
                .id("msg1")
                .chatRoomId(chatRoomId)
                .senderId(senderId1)
                .senderName("사용자1")
                .content("첫 번째 테스트 메시지입니다.")
                .sentAt(now.minusMinutes(5))
                .read(true)
                .readAt(now)
                .build();
        
        when(chatMessageService.markMessageAsRead("msg1")).thenReturn(readMessage);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/messages/msg1/read")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("msg1")))
                .andExpect(jsonPath("$.read", is(true)))
                .andExpect(jsonPath("$.readAt").exists());
    }

    @Test
    @DisplayName("채팅방의 최근 메시지 조회")
    void testGetLatestMessages() throws Exception {
        // Given
        when(chatMessageService.getLatestMessages(chatRoomId, 1))
                .thenReturn(List.of(message2));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/messages/" + chatRoomId + "/latest")
                .contentType(MediaType.APPLICATION_JSON)
                .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("msg2")));
    }
} 