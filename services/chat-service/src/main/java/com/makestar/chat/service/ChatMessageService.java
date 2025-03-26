package com.makestar.chat.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.makestar.chat.dto.ChatMessageDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅 메시지 관리를 위한 서비스 인터페이스
 */
public interface ChatMessageService {
    
    /**
     * 새로운 채팅 메시지를 저장합니다.
     * @param messageDto 저장할 메시지 정보
     * @return 저장된 메시지 정보
     */
    ChatMessageDto saveMessage(ChatMessageDto messageDto);
    
    /**
     * 채팅방의 메시지 목록을 페이징하여 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @param pageable 페이징 정보
     * @return 페이징된 메시지 목록
     */
    Page<ChatMessageDto> getChatMessages(String chatRoomId, Pageable pageable);
    
    /**
     * 특정 시간 이후의 채팅방 메시지를 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @param since 기준 시간
     * @return 메시지 목록
     */
    List<ChatMessageDto> getMessagesSince(String chatRoomId, LocalDateTime since);
    
    /**
     * 메시지 ID로 메시지를 조회합니다.
     * @param messageId 메시지 ID
     * @return 메시지 정보
     */
    ChatMessageDto getMessageById(String messageId);
    
    /**
     * 메시지를 읽음 상태로 표시합니다.
     * @param messageId 메시지 ID
     * @param userId 읽은 사용자 ID
     */
    void markAsRead(String messageId, String userId);
    
    /**
     * 채팅방의 모든 메시지를 읽음 상태로 표시합니다.
     * @param chatRoomId 채팅방 ID
     * @param userId 읽은 사용자 ID
     */
    void markAllAsRead(String chatRoomId, String userId);
    
    /**
     * 채팅방의 읽지 않은 메시지 개수를 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 읽지 않은 메시지 개수
     */
    long countUnreadMessages(String chatRoomId, String userId);
    
    /**
     * 채팅방의 최근 메시지를 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @return 최근 메시지 정보
     */
    ChatMessageDto getLatestMessage(String chatRoomId);
    
    /**
     * 메시지 내용으로 메시지를 검색합니다.
     * @param chatRoomId 채팅방 ID
     * @param keyword 검색 키워드
     * @return 검색된 메시지 목록
     */
    List<ChatMessageDto> searchMessages(String chatRoomId, String keyword);
    
    /**
     * 메시지를 삭제합니다.
     * @param messageId 삭제할 메시지 ID
     */
    void deleteMessage(String messageId);
} 