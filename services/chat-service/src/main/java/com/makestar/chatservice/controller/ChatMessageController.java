package com.makestar.chatservice.controller;

import com.makestar.chatservice.dto.ChatMessageDto;
import com.makestar.chatservice.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅 메시지 관련 REST API 컨트롤러
 * 채팅 메시지의 조회, 생성, 수정, 삭제 등의 HTTP 요청을 처리합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 새로운 채팅 메시지를 전송합니다.
     * @param messageDto 전송할 메시지 정보
     * @return 저장된 메시지 정보
     */
    @PostMapping
    public ResponseEntity<ChatMessageDto> sendMessage(@RequestBody ChatMessageDto messageDto) {
        log.info("Sending message to room: {} from user: {}", messageDto.getChatRoomId(), messageDto.getSenderId());
        ChatMessageDto savedMessage = chatMessageService.saveMessage(messageDto);
        return new ResponseEntity<>(savedMessage, HttpStatus.CREATED);
    }

    /**
     * 특정 채팅방의 메시지 목록을 조회합니다.
     *
     * @param chatRoomId 채팅방 ID
     * @param pageable 페이지네이션 정보
     * @return 채팅 메시지 목록
     */
    @GetMapping("/room/{chatRoomId}")
    public Page<ChatMessageDto> getMessagesByChatRoomId(
            @PathVariable String chatRoomId,
            @PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Getting messages for room: {}", chatRoomId);
        return chatMessageService.getChatMessages(chatRoomId, pageable);
    }

    /**
     * 특정 시간 이후의 채팅방 메시지를 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @param since 기준 시간
     * @return 메시지 목록
     */
    @GetMapping("/room/{chatRoomId}/since")
    public ResponseEntity<List<ChatMessageDto>> getMessagesSince(@PathVariable String chatRoomId,
                                                               @RequestParam LocalDateTime since) {
        log.info("Getting messages for room: {} since: {}", chatRoomId, since);
        List<ChatMessageDto> messages = chatMessageService.getMessagesSince(chatRoomId, since);
        return ResponseEntity.ok(messages);
    }

    /**
     * 메시지 ID로 메시지를 조회합니다.
     * @param messageId 메시지 ID
     * @return 메시지 정보
     */
    @GetMapping("/{messageId}")
    public ResponseEntity<ChatMessageDto> getMessageById(@PathVariable String messageId) {
        log.info("Getting message: {}", messageId);
        ChatMessageDto message = chatMessageService.getMessageById(messageId);
        return ResponseEntity.ok(message);
    }

    /**
     * 메시지 읽음 상태를 업데이트합니다.
     *
     * @param messageId 메시지 ID
     * @param userId 사용자 ID
     * @return 업데이트된 메시지 정보
     */
    @PutMapping("/{messageId}/read")
    public ChatMessageDto markMessageAsRead(
            @PathVariable String messageId,
            @RequestParam String userId) {
        log.info("Marking message: {} as read by user: {}", messageId, userId);
        chatMessageService.markAsRead(messageId, userId);
        return chatMessageService.getMessageById(messageId);
    }

    /**
     * 특정 채팅방의 모든 메시지를 읽음 처리합니다.
     *
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 업데이트된 메시지 수
     */
    @PutMapping("/room/{chatRoomId}/read-all")
    public ResponseEntity<Long> markAllMessagesAsRead(
            @PathVariable String chatRoomId,
            @RequestParam String userId) {
        log.info("Marking all messages as read in room: {} by user: {}", chatRoomId, userId);
        chatMessageService.markAllAsRead(chatRoomId, userId);
        return ResponseEntity.ok(chatMessageService.countUnreadMessages(chatRoomId, userId));
    }

    /**
     * 채팅방의 읽지 않은 메시지 개수를 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 읽지 않은 메시지 개수
     */
    @GetMapping("/room/{chatRoomId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String chatRoomId,
                                             @RequestParam String userId) {
        log.info("Counting unread messages in room: {} for user: {}", chatRoomId, userId);
        long count = chatMessageService.countUnreadMessages(chatRoomId, userId);
        return ResponseEntity.ok(count);
    }

    /**
     * 채팅방의 최근 메시지를 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @return 최근 메시지 정보
     */
    @GetMapping("/room/{chatRoomId}/latest")
    public ResponseEntity<ChatMessageDto> getLatestMessage(@PathVariable String chatRoomId) {
        log.info("Getting latest message for room: {}", chatRoomId);
        ChatMessageDto message = chatMessageService.getLatestMessage(chatRoomId);
        if (message == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(message);
    }

    /**
     * 메시지 내용으로 메시지를 검색합니다.
     * @param chatRoomId 채팅방 ID
     * @param keyword 검색 키워드
     * @return 검색된 메시지 목록
     */
    @GetMapping("/room/{chatRoomId}/search")
    public ResponseEntity<List<ChatMessageDto>> searchMessages(@PathVariable String chatRoomId,
                                                             @RequestParam String keyword) {
        log.info("Searching messages in room: {} with keyword: {}", chatRoomId, keyword);
        List<ChatMessageDto> messages = chatMessageService.searchMessages(chatRoomId, keyword);
        return ResponseEntity.ok(messages);
    }

    /**
     * 메시지를 삭제합니다.
     * @param messageId 삭제할 메시지 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable String messageId) {
        log.info("Deleting message: {}", messageId);
        chatMessageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
} 