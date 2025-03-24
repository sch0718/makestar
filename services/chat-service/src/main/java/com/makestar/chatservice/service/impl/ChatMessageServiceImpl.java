package com.makestar.chatservice.service.impl;

import com.makestar.chatservice.client.UserServiceClient;
import com.makestar.chatservice.dto.ChatMessageDto;
import com.makestar.chatservice.model.ChatMessage;
import com.makestar.chatservice.repository.ChatMessageRepository;
import com.makestar.chatservice.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserServiceClient userServiceClient;

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

    @Override
    public Page<ChatMessageDto> getChatMessages(String chatRoomId, Pageable pageable) {
        log.info("Getting chat messages for room: {}", chatRoomId);
        
        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(chatRoomId, pageable);
        return messages.map(this::enrichMessageWithSenderInfo);
    }

    @Override
    public List<ChatMessageDto> getMessagesSince(String chatRoomId, LocalDateTime since) {
        log.info("Getting messages for room: {} since: {}", chatRoomId, since);
        
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdAndSentAtAfterOrderBySentAtAsc(chatRoomId, since);
        return messages.stream()
                .map(this::enrichMessageWithSenderInfo)
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageDto getMessageById(String messageId) {
        log.info("Getting message by id: {}", messageId);
        
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));
        
        return enrichMessageWithSenderInfo(message);
    }

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

    @Override
    public long countUnreadMessages(String chatRoomId, String userId) {
        log.info("Counting unread messages in room: {} for user: {}", chatRoomId, userId);
        return chatMessageRepository.countUnreadMessages(chatRoomId, userId);
    }

    @Override
    public ChatMessageDto getLatestMessage(String chatRoomId) {
        log.info("Getting latest message for room: {}", chatRoomId);
        
        List<ChatMessage> messages = chatMessageRepository.findLatestMessages(chatRoomId, org.springframework.data.domain.PageRequest.of(0, 1));
        if (messages.isEmpty()) {
            return null;
        }
        
        return enrichMessageWithSenderInfo(messages.get(0));
    }

    @Override
    public List<ChatMessageDto> searchMessages(String chatRoomId, String keyword) {
        log.info("Searching messages in room: {} with keyword: {}", chatRoomId, keyword);
        
        List<ChatMessage> messages = chatMessageRepository.findByContentContainingIgnoreCaseAndChatRoomId(keyword, chatRoomId);
        return messages.stream()
                .map(this::enrichMessageWithSenderInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMessage(String messageId) {
        log.info("Deleting message: {}", messageId);
        
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));
        
        chatMessageRepository.delete(message);
    }
    
    // 사용자 정보 추가 메서드
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