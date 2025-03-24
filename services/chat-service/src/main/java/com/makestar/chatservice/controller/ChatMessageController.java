package com.makestar.chatservice.controller;

import com.makestar.chatservice.dto.ChatMessageDto;
import com.makestar.chatservice.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat/messages")
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @PostMapping
    public ResponseEntity<ChatMessageDto> sendMessage(@RequestBody ChatMessageDto messageDto) {
        log.info("Sending message to room: {} from user: {}", messageDto.getChatRoomId(), messageDto.getSenderId());
        ChatMessageDto savedMessage = chatMessageService.saveMessage(messageDto);
        return new ResponseEntity<>(savedMessage, HttpStatus.CREATED);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Page<ChatMessageDto>> getChatMessages(
            @PathVariable String roomId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting messages for room: {}", roomId);
        Page<ChatMessageDto> messages = chatMessageService.getChatMessages(roomId, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/room/{roomId}/since")
    public ResponseEntity<List<ChatMessageDto>> getMessagesSince(
            @PathVariable String roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        log.info("Getting messages for room: {} since: {}", roomId, since);
        List<ChatMessageDto> messages = chatMessageService.getMessagesSince(roomId, since);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<ChatMessageDto> getMessageById(@PathVariable String messageId) {
        log.info("Getting message: {}", messageId);
        ChatMessageDto message = chatMessageService.getMessageById(messageId);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String messageId, @RequestParam String userId) {
        log.info("Marking message: {} as read by user: {}", messageId, userId);
        chatMessageService.markAsRead(messageId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/room/{roomId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable String roomId, @RequestParam String userId) {
        log.info("Marking all messages as read in room: {} by user: {}", roomId, userId);
        chatMessageService.markAllAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/room/{roomId}/unread")
    public ResponseEntity<Long> countUnreadMessages(@PathVariable String roomId, @RequestParam String userId) {
        log.info("Counting unread messages in room: {} for user: {}", roomId, userId);
        long count = chatMessageService.countUnreadMessages(roomId, userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/room/{roomId}/latest")
    public ResponseEntity<ChatMessageDto> getLatestMessage(@PathVariable String roomId) {
        log.info("Getting latest message for room: {}", roomId);
        ChatMessageDto message = chatMessageService.getLatestMessage(roomId);
        if (message == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(message);
    }

    @GetMapping("/room/{roomId}/search")
    public ResponseEntity<List<ChatMessageDto>> searchMessages(
            @PathVariable String roomId,
            @RequestParam String keyword) {
        log.info("Searching messages in room: {} with keyword: {}", roomId, keyword);
        List<ChatMessageDto> messages = chatMessageService.searchMessages(roomId, keyword);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable String messageId) {
        log.info("Deleting message: {}", messageId);
        chatMessageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
} 