package com.makestar.chatservice.controller;

import com.makestar.chatservice.dto.ChatRoomDto;
import com.makestar.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody ChatRoomDto chatRoomDto,
                                                     @RequestParam String creatorId) {
        log.info("Creating chat room: {}, creator: {}", chatRoomDto.getName(), creatorId);
        ChatRoomDto createdRoom = chatRoomService.createChatRoom(chatRoomDto, creatorId);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomDto> getChatRoomById(@PathVariable String roomId) {
        log.info("Getting chat room: {}", roomId);
        ChatRoomDto chatRoom = chatRoomService.getChatRoomById(roomId);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomDto>> getAllChatRooms() {
        log.info("Getting all chat rooms");
        List<ChatRoomDto> chatRooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatRoomDto>> getChatRoomsByUserId(@PathVariable String userId) {
        log.info("Getting chat rooms for user: {}", userId);
        List<ChatRoomDto> chatRooms = chatRoomService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChatRoomDto>> searchChatRoomsByName(@RequestParam String name) {
        log.info("Searching chat rooms by name: {}", name);
        List<ChatRoomDto> chatRooms = chatRoomService.searchChatRoomsByName(name);
        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<Void> joinChatRoom(@PathVariable String roomId, @RequestParam String userId) {
        log.info("User {} joining chat room {}", userId, roomId);
        chatRoomService.joinChatRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable String roomId, @RequestParam String userId) {
        log.info("User {} leaving chat room {}", userId, roomId);
        chatRoomService.leaveChatRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/direct")
    public ResponseEntity<ChatRoomDto> getOrCreateDirectChatRoom(@RequestParam String userIdA,
                                                                @RequestParam String userIdB) {
        log.info("Getting or creating direct chat room between users {} and {}", userIdA, userIdB);
        ChatRoomDto chatRoom = chatRoomService.getOrCreateDirectChatRoom(userIdA, userIdB);
        return ResponseEntity.ok(chatRoom);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<ChatRoomDto> updateChatRoom(@PathVariable String roomId,
                                                     @RequestBody ChatRoomDto chatRoomDto) {
        log.info("Updating chat room {}", roomId);
        ChatRoomDto updatedRoom = chatRoomService.updateChatRoom(roomId, chatRoomDto);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable String roomId) {
        log.info("Deleting chat room {}", roomId);
        chatRoomService.deleteChatRoom(roomId);
        return ResponseEntity.noContent().build();
    }
} 