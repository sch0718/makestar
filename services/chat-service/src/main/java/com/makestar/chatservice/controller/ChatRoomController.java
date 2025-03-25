package com.makestar.chatservice.controller;

import com.makestar.chatservice.dto.AddParticipantsRequest;
import com.makestar.chatservice.dto.ChatRoomDto;
import com.makestar.chatservice.dto.CreateChatRoomRequest;
import com.makestar.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅방 관련 REST API 컨트롤러
 * 채팅방의 생성, 조회, 수정, 삭제 등의 HTTP 요청을 처리합니다.
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 새로운 채팅방을 생성합니다.
     *
     * @param request 채팅방 생성 요청 정보
     * @return 생성된 채팅방 정보
     */
    @PostMapping
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody CreateChatRoomRequest request) {
        log.info("Creating chat room with request: {}", request);
        ChatRoomDto chatRoom = chatRoomService.createChatRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatRoom);
    }

    /**
     * 특정 사용자가 참여한 모든 채팅방을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 채팅방 목록
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatRoomDto>> getChatRoomsByUserId(@PathVariable String userId) {
        log.info("Getting chat rooms for user: {}", userId);
        List<ChatRoomDto> chatRooms = chatRoomService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok(chatRooms);
    }

    /**
     * 채팅방 ID로 특정 채팅방을 조회합니다.
     *
     * @param roomId 채팅방 ID
     * @return 채팅방 정보
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomDto> getChatRoomById(@PathVariable String roomId) {
        log.info("Getting chat room: {}", roomId);
        ChatRoomDto chatRoom = chatRoomService.getChatRoomById(roomId);
        return ResponseEntity.ok(chatRoom);
    }

    /**
     * 채팅방에 새로운 참여자를 추가합니다.
     *
     * @param roomId 채팅방 ID
     * @param request 참여자 추가 요청 정보
     * @return 업데이트된 채팅방 정보
     */
    @PostMapping("/{roomId}/participants")
    public ResponseEntity<ChatRoomDto> addParticipants(
            @PathVariable String roomId,
            @RequestBody AddParticipantsRequest request) {
        log.info("Adding participants to room: {} with request: {}", roomId, request);
        ChatRoomDto chatRoom = chatRoomService.addParticipants(roomId, request.getParticipantIds());
        return ResponseEntity.ok(chatRoom);
    }

    /**
     * 채팅방에서 참여자를 제거합니다.
     *
     * @param roomId 채팅방 ID
     * @param userId 제거할 사용자 ID
     * @return 업데이트된 채팅방 정보
     */
    @DeleteMapping("/{roomId}/participants/{userId}")
    public ResponseEntity<ChatRoomDto> removeParticipant(
            @PathVariable String roomId,
            @PathVariable String userId) {
        log.info("Removing participant: {} from room: {}", userId, roomId);
        ChatRoomDto chatRoom = chatRoomService.removeParticipant(roomId, userId);
        return ResponseEntity.ok(chatRoom);
    }

    /**
     * 모든 채팅방 목록을 조회합니다.
     * @param pageable 페이징 정보
     * @return 페이징된 채팅방 목록
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomDto>> getAllChatRooms(Pageable pageable) {
        log.info("Getting all chat rooms");
        List<ChatRoomDto> chatRooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(chatRooms);
    }

    /**
     * 채팅방 이름으로 채팅방을 검색합니다.
     * @param name 검색할 채팅방 이름
     * @return 검색된 채팅방 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<ChatRoomDto>> searchChatRooms(@RequestParam String name) {
        log.info("Searching chat rooms by name: {}", name);
        List<ChatRoomDto> chatRooms = chatRoomService.searchChatRoomsByName(name);
        return ResponseEntity.ok(chatRooms);
    }

    /**
     * 채팅방에 사용자를 참여시킵니다.
     * @param chatRoomId 참여할 채팅방 ID
     * @param userId 참여할 사용자 ID
     * @return 참여 결과
     */
    @PostMapping("/{chatRoomId}/join")
    public ResponseEntity<Void> joinChatRoom(@PathVariable String chatRoomId,
                                           @RequestParam String userId) {
        log.info("User {} joining chat room {}", userId, chatRoomId);
        chatRoomService.joinChatRoom(chatRoomId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방에서 사용자를 나가게 합니다.
     * @param chatRoomId 나갈 채팅방 ID
     * @param userId 나갈 사용자 ID
     * @return 퇴장 결과
     */
    @PostMapping("/{chatRoomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable String chatRoomId,
                                            @RequestParam String userId) {
        log.info("User {} leaving chat room {}", userId, chatRoomId);
        chatRoomService.leaveChatRoom(chatRoomId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 1:1 채팅방을 생성하거나 조회합니다.
     * @param userId1 첫 번째 사용자 ID
     * @param userId2 두 번째 사용자 ID
     * @return 1:1 채팅방 정보
     */
    @GetMapping("/direct")
    public ResponseEntity<ChatRoomDto> getDirectChatRoom(@RequestParam String userId1,
                                                        @RequestParam String userId2) {
        log.info("Getting or creating direct chat room between users {} and {}", userId1, userId2);
        ChatRoomDto chatRoom = chatRoomService.getOrCreateDirectChatRoom(userId1, userId2);
        return ResponseEntity.ok(chatRoom);
    }

    /**
     * 채팅방 정보를 수정합니다.
     * @param chatRoomId 수정할 채팅방 ID
     * @param chatRoomDto 수정할 채팅방 정보
     * @return 수정된 채팅방 정보
     */
    @PutMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomDto> updateChatRoom(@PathVariable String chatRoomId,
                                                     @RequestBody ChatRoomDto chatRoomDto) {
        log.info("Updating chat room {}", chatRoomId);
        ChatRoomDto updatedRoom = chatRoomService.updateChatRoom(chatRoomId, chatRoomDto);
        return ResponseEntity.ok(updatedRoom);
    }

    /**
     * 채팅방을 삭제합니다.
     * @param chatRoomId 삭제할 채팅방 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable String chatRoomId) {
        log.info("Deleting chat room {}", chatRoomId);
        chatRoomService.deleteChatRoom(chatRoomId);
        return ResponseEntity.noContent().build();
    }
} 