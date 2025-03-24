package test.java.com.makestar.chatservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makestar.chatservice.dto.ChatRoomDto;
import com.makestar.chatservice.dto.ParticipantDto;
import com.makestar.chatservice.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatRoomController.class)
public class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatRoomService chatRoomService;

    @Autowired
    private ObjectMapper objectMapper;

    private ChatRoomDto chatRoom1;
    private ChatRoomDto chatRoom2;
    private String userId1 = "user1";
    private String userId2 = "user2";

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        Set<ParticipantDto> participants1 = new HashSet<>();
        participants1.add(new ParticipantDto(userId1, "사용자1", now));
        participants1.add(new ParticipantDto(userId2, "사용자2", now));
        
        Set<ParticipantDto> participants2 = new HashSet<>();
        participants2.add(new ParticipantDto(userId1, "사용자1", now));
        
        chatRoom1 = ChatRoomDto.builder()
                .id("room1")
                .name("테스트 채팅방 1")
                .type("GROUP")
                .participants(participants1)
                .createdAt(now)
                .updatedAt(now)
                .build();
                
        chatRoom2 = ChatRoomDto.builder()
                .id("room2")
                .name("테스트 채팅방 2")
                .type("DIRECT")
                .participants(participants2)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    @DisplayName("채팅방 ID로 조회")
    void testGetChatRoomById() throws Exception {
        // Given
        when(chatRoomService.getChatRoomById("room1")).thenReturn(chatRoom1);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/chat-rooms/room1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("room1")))
                .andExpect(jsonPath("$.name", is("테스트 채팅방 1")))
                .andExpect(jsonPath("$.type", is("GROUP")))
                .andExpect(jsonPath("$.participants", hasSize(2)));
    }

    @Test
    @DisplayName("사용자의 모든 채팅방 조회")
    void testGetChatRoomsByUserId() throws Exception {
        // Given
        when(chatRoomService.getChatRoomsByUserId(userId1)).thenReturn(Arrays.asList(chatRoom1, chatRoom2));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/chat-rooms/user/" + userId1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("room1")))
                .andExpect(jsonPath("$[1].id", is("room2")));
    }

    @Test
    @DisplayName("새 채팅방 생성")
    void testCreateChatRoom() throws Exception {
        // Given
        ChatRoomDto newRoomRequest = ChatRoomDto.builder()
                .name("새 채팅방")
                .type("GROUP")
                .participants(new HashSet<>())
                .build();
        
        when(chatRoomService.createChatRoom(any(ChatRoomDto.class))).thenReturn(chatRoom1);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/chat-rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRoomRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("room1")))
                .andExpect(jsonPath("$.name", is("테스트 채팅방 1")));
    }

    @Test
    @DisplayName("두 사용자 간 1:1 채팅방 찾기 또는 생성")
    void testFindOrCreateDirectChatRoom() throws Exception {
        // Given
        when(chatRoomService.findOrCreateDirectChatRoom(userId1, userId2)).thenReturn(chatRoom1);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/chat-rooms/direct")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId1", userId1)
                .param("userId2", userId2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("room1")))
                .andExpect(jsonPath("$.participants", hasSize(2)));
    }

    @Test
    @DisplayName("채팅방에 참가자 추가")
    void testAddParticipant() throws Exception {
        // Given
        ParticipantDto newParticipant = new ParticipantDto("user3", "사용자3", LocalDateTime.now());
        
        Set<ParticipantDto> updatedParticipants = new HashSet<>(chatRoom1.getParticipants());
        updatedParticipants.add(newParticipant);
        
        ChatRoomDto updatedRoom = ChatRoomDto.builder()
                .id(chatRoom1.getId())
                .name(chatRoom1.getName())
                .type(chatRoom1.getType())
                .participants(updatedParticipants)
                .createdAt(chatRoom1.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(chatRoomService.addParticipant(anyString(), any(ParticipantDto.class))).thenReturn(updatedRoom);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/chat-rooms/room1/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newParticipant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants", hasSize(3)));
    }

    @Test
    @DisplayName("채팅방에서 참가자 제거")
    void testRemoveParticipant() throws Exception {
        // Given
        Set<ParticipantDto> updatedParticipants = new HashSet<>();
        updatedParticipants.add(new ParticipantDto(userId1, "사용자1", LocalDateTime.now()));
        
        ChatRoomDto updatedRoom = ChatRoomDto.builder()
                .id(chatRoom1.getId())
                .name(chatRoom1.getName())
                .type(chatRoom1.getType())
                .participants(updatedParticipants)
                .createdAt(chatRoom1.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(chatRoomService.removeParticipant("room1", userId2)).thenReturn(updatedRoom);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/chat-rooms/room1/participants/" + userId2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants", hasSize(1)))
                .andExpect(jsonPath("$.participants[0].userId", is(userId1)));
    }

    @Test
    @DisplayName("채팅방 삭제")
    void testDeleteChatRoom() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/chat-rooms/room1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
} 