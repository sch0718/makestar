package com.makestar.chatservice.service;

import com.makestar.chatservice.dto.ChatMessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageService {
    
    // 채팅 메시지 저장
    ChatMessageDto saveMessage(ChatMessageDto messageDto);
    
    // 채팅방의 메시지 목록 조회 (페이징)
    Page<ChatMessageDto> getChatMessages(String chatRoomId, Pageable pageable);
    
    // 특정 시간 이후의 채팅방 메시지 조회
    List<ChatMessageDto> getMessagesSince(String chatRoomId, LocalDateTime since);
    
    // 메시지 조회
    ChatMessageDto getMessageById(String messageId);
    
    // 메시지 읽음 처리
    void markAsRead(String messageId, String userId);
    
    // 채팅방의 모든 메시지 읽음 처리
    void markAllAsRead(String chatRoomId, String userId);
    
    // 채팅방의 읽지 않은 메시지 개수 조회
    long countUnreadMessages(String chatRoomId, String userId);
    
    // 채팅방의 최근 메시지 조회
    ChatMessageDto getLatestMessage(String chatRoomId);
    
    // 메시지 내용으로 검색
    List<ChatMessageDto> searchMessages(String chatRoomId, String keyword);
    
    // 메시지 삭제
    void deleteMessage(String messageId);
} 