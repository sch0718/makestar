package test.java.com.makestar.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makestar.userservice.dto.FriendRequestDto;
import com.makestar.userservice.dto.UserDto;
import com.makestar.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private FriendRequestDto friendRequestDto;
    private List<UserDto> userDtoList;
    private List<FriendRequestDto> friendRequestDtoList;

    @BeforeEach
    void setUp() {
        // 테스트용 UserDto 생성
        userDto = UserDto.builder()
                .id("1")
                .username("testUser")
                .email("test@example.com")
                .status("ONLINE")
                .build();

        // 테스트용 UserDto 리스트 생성
        UserDto userDto2 = UserDto.builder()
                .id("2")
                .username("anotherUser")
                .email("another@example.com")
                .status("OFFLINE")
                .build();
        userDtoList = Arrays.asList(userDto, userDto2);

        // 테스트용 FriendRequestDto 생성
        friendRequestDto = FriendRequestDto.builder()
                .id("1")
                .senderId("1")
                .receiverId("2")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 테스트용 FriendRequestDto 리스트 생성
        friendRequestDtoList = Arrays.asList(friendRequestDto);
    }

    @Test
    @DisplayName("사용자 ID로 사용자 조회")
    void testGetUserById() throws Exception {
        when(userService.getUserById("1")).thenReturn(userDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.status").value("ONLINE"));
    }

    @Test
    @DisplayName("사용자명으로 사용자 조회")
    void testGetUserByUsername() throws Exception {
        when(userService.getUserByUsername("testUser")).thenReturn(userDto);

        mockMvc.perform(get("/api/users/username/testUser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    @DisplayName("키워드로 사용자 검색")
    void testSearchUsers() throws Exception {
        when(userService.searchUsers("user")).thenReturn(userDtoList);

        mockMvc.perform(get("/api/users/search")
                .param("keyword", "user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[0].username").value("testUser"))
                .andExpect(jsonPath("$[1].username").value("anotherUser"));
    }

    @Test
    @DisplayName("사용자 프로필 업데이트")
    void testUpdateUserProfile() throws Exception {
        UserDto updatedUserDto = UserDto.builder()
                .id("1")
                .username("updatedUser")
                .email("updated@example.com")
                .status("ONLINE")
                .build();

        when(userService.updateUserProfile(anyString(), any(UserDto.class))).thenReturn(updatedUserDto);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("updatedUser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    @DisplayName("사용자 상태 업데이트")
    void testUpdateUserStatus() throws Exception {
        UserDto updatedUserDto = UserDto.builder()
                .id("1")
                .username("testUser")
                .email("test@example.com")
                .status("AWAY")
                .build();

        when(userService.updateUserStatus("1", "AWAY")).thenReturn(updatedUserDto);

        mockMvc.perform(put("/api/users/1/status")
                .param("status", "AWAY"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("AWAY"));
    }

    @Test
    @DisplayName("사용자의 친구 목록 조회")
    void testGetFriends() throws Exception {
        when(userService.getFriends("1")).thenReturn(userDtoList);

        mockMvc.perform(get("/api/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }

    @Test
    @DisplayName("친구 요청 보내기")
    void testSendFriendRequest() throws Exception {
        when(userService.sendFriendRequest("1", "2")).thenReturn(friendRequestDto);

        mockMvc.perform(post("/api/users/1/friends/requests")
                .param("receiverId", "2"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.senderId").value("1"))
                .andExpect(jsonPath("$.receiverId").value("2"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("받은 친구 요청 조회")
    void testGetReceivedFriendRequests() throws Exception {
        when(userService.getReceivedFriendRequests("1")).thenReturn(friendRequestDtoList);

        mockMvc.perform(get("/api/users/1/friends/requests/received"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].senderId").value("1"))
                .andExpect(jsonPath("$[0].receiverId").value("2"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("보낸 친구 요청 조회")
    void testGetSentFriendRequests() throws Exception {
        when(userService.getSentFriendRequests("1")).thenReturn(friendRequestDtoList);

        mockMvc.perform(get("/api/users/1/friends/requests/sent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].senderId").value("1"))
                .andExpect(jsonPath("$[0].receiverId").value("2"));
    }

    @Test
    @DisplayName("친구 요청 수락")
    void testAcceptFriendRequest() throws Exception {
        FriendRequestDto acceptedRequest = FriendRequestDto.builder()
                .id("1")
                .senderId("1")
                .receiverId("2")
                .status("ACCEPTED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.acceptFriendRequest("1")).thenReturn(acceptedRequest);

        mockMvc.perform(put("/api/users/friends/requests/1/accept"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("친구 요청 거절")
    void testRejectFriendRequest() throws Exception {
        FriendRequestDto rejectedRequest = FriendRequestDto.builder()
                .id("1")
                .senderId("1")
                .receiverId("2")
                .status("REJECTED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.rejectFriendRequest("1")).thenReturn(rejectedRequest);

        mockMvc.perform(put("/api/users/friends/requests/1/reject"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @DisplayName("친구 삭제")
    void testRemoveFriend() throws Exception {
        doNothing().when(userService).removeFriend("1", "2");

        mockMvc.perform(delete("/api/users/1/friends/2"))
                .andExpect(status().isNoContent());
    }
} 