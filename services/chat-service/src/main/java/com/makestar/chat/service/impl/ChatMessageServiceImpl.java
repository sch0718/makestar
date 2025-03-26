package com.makestar.chat.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.makestar.chat.client.UserServiceClient;
import com.makestar.chat.dto.ChatMessageDto;
import com.makestar.chat.model.ChatMessage;
import com.makestar.chat.repository.ChatMessageRepository;
import com.makestar.chat.service.ChatMessageService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ChatMessageService 인터페이스의 구현 클래스
 * 채팅 메시지의 저장, 조회, 읽음 처리 등 메시지 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserServiceClient userServiceClient;

    /**
     * 새로운 채팅 메시지를 저장합니다.
     * 
     * @param messageDto 저장할 메시지 정보
     * @return 저장된 메시지 정보
     */
    @Override
    @Transactional
    public ChatMessageDto saveMessage(ChatMessageDto messageDto) {
        log.info("Saving chat message to room: {} from user: {}", messageDto.getChatRoomId(), messageDto.getSenderId());
        
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(messageDto.getChatRoomId())
                .senderId(messageDto.getSenderId())
                .content(messageDto.getContent())
                .type(ChatMessage.MessageType.valueOf(messageDto.getType()))
                .build();
        
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        return ChatMessageDto.fromEntity(savedMessage);
    }

    /**
     * 채팅방의 메시지 목록을 페이징하여 조회합니다.
     * 각 메시지에 발신자 정보를 포함하여 반환합니다.
     * 
     * @param chatRoomId 채팅방 ID
     * @param pageable 페이징 정보
     * @return 페이징된 메시지 목록
     */
    @Override
    public Page<ChatMessageDto> getChatMessages(String chatRoomId, Pageable pageable) {
        log.info("Getting chat messages for room: {}", chatRoomId);
        
        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(chatRoomId, pageable);
        return messages.map(this::enrichMessageWithSenderInfo);
    }

    /**
     * 특정 시간 이후의 채팅방 메시지를 조회합니다.
     * 각 메시지에 발신자 정보를 포함하여 반환합니다.
     * 
     * @param chatRoomId 채팅방 ID
     * @param since 기준 시간
     * @return 메시지 목록
     */
    @Override
    public List<ChatMessageDto> getMessagesSince(String chatRoomId, LocalDateTime since) {
        log.info("Getting messages for room: {} since: {}", chatRoomId, since);
        
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdAndSentAtAfterOrderBySentAtAsc(chatRoomId, since);
        return messages.stream()
                .map(this::enrichMessageWithSenderInfo)
                .collect(Collectors.toList());
    }

    /**
     * 메시지 ID로 메시지를 조회합니다.
     * 발신자 정보를 포함하여 반환합니다.
     * 
     * @param messageId 메시지 ID
     * @return 메시지 정보
     * @throws EntityNotFoundException 메시지를 찾을 수 없는 경우
     */
    @Override
    public ChatMessageDto getMessageById(String messageId) {
        log.info("Getting message by id: {}", messageId);
        
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));
        
        return enrichMessageWithSenderInfo(message);
    }

    /**
     * 메시지를 읽음 상태로 표시합니다.
     * 본인이 보낸 메시지는 읽음 처리하지 않습니다.
     * 
     * @param messageId 메시지 ID
     * @param userId 읽은 사용자 ID
     * @throws EntityNotFoundException 메시지를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public void markAsRead(String messageId, String userId) {
        log.info("Marking message: {} as read by user: {}", messageId, userId);
        
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));
        
        // 본인이 보낸 메시지는 읽음 처리 불필요
        if (!message.getSenderId().equals(userId)) {
            message.markAsRead();
            chatMessageRepository.save(message);
        }
    }

    /**
     * 채팅방의 모든 메시지를 읽음 상태로 표시합니다.
     * 본인이 보낸 메시지는 제외하고 처리합니다.
     * 
     * @param chatRoomId 채팅방 ID
     * @param userId 읽은 사용자 ID
     */
    @Override
    @Transactional
    public void markAllAsRead(String chatRoomId, String userId) {
        log.info("Marking all messages as read in room: {} by user: {}", chatRoomId, userId);
        
        List<ChatMessage> unreadMessages = chatMessageRepository.findByChatRoomIdAndSentAtAfterOrderBySentAtAsc(chatRoomId, LocalDateTime.now().minusYears(1))
                .stream()
                .filter(message -> !message.getSenderId().equals(userId) && !message.isRead())
                .collect(Collectors.toList());
        
        unreadMessages.forEach(message -> {
            message.markAsRead();
            chatMessageRepository.save(message);
        });
    }

    /**
     * 채팅방의 읽지 않은 메시지 개수를 조회합니다.
     * 
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 읽지 않은 메시지 개수
     */
    @Override
    public long countUnreadMessages(String chatRoomId, String userId) {
        log.info("Counting unread messages in room: {} for user: {}", chatRoomId, userId);
        return chatMessageRepository.countUnreadMessages(chatRoomId, userId);
    }

    @Override
    public ChatMessageDto getLatestMessage(String chatRoomId) {
        log.info("Getting latest message for room: {}", chatRoomId);
        
        return chatMessageRepository.findFirstByChatRoomIdOrderBySentAtDesc(chatRoomId)
                .map(this::enrichMessageWithSenderInfo)
                .orElse(null);
    }

    @Override
    public List<ChatMessageDto> searchMessages(String chatRoomId, String keyword) {
        log.info("Searching messages in room: {} with keyword: {}", chatRoomId, keyword);
        
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdAndContentContainingIgnoreCaseOrderBySentAtDesc(chatRoomId, keyword);
        return messages.stream()
                .map(this::enrichMessageWithSenderInfo)
                .collect(Collectors.toList());
    }

    /**
     * 지정된 메시지를 삭제합니다.
     * 메시지가 존재하지 않는 경우 EntityNotFoundException을 발생시킵니다.
     * 
     * @param messageId 삭제할 메시지 ID
     * @throws EntityNotFoundException 메시지를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public void deleteMessage(String messageId) {
        log.info("Deleting message: {}", messageId);
        
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));
        
        chatMessageRepository.delete(message);
    }
    
    /**
     * 메시지에 발신자 정보를 추가합니다.
     * User Service를 통해 발신자의 사용자 정보를 조회하여 메시지 DTO에 포함시킵니다.
     * 
     * @param message 발신자 정보를 추가할 메시지
     * @return 발신자 정보가 포함된 메시지 DTO
     */
    private ChatMessageDto enrichMessageWithSenderInfo(ChatMessage message) {
        String senderName = "Unknown User";
        
        try {
            ResponseEntity<Map<String, Object>> userResponse = userServiceClient.getUserById(message.getSenderId());
            if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
                Map<String, Object> userData = userResponse.getBody();
                String username = (String) userData.get("username");
                if (username != null) {
                    senderName = username;
                }
            }
        } catch (Exception e) {
            log.error("Error fetching sender info: {}", e.getMessage());
        }
        
        return ChatMessageDto.fromEntityWithSenderName(message, senderName);
    }
} 