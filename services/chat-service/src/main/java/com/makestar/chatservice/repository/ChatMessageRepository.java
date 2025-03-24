package com.makestar.chatservice.repository;

import com.makestar.chatservice.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    // 채팅방의 메시지 목록 조회 (페이징)
    Page<ChatMessage> findByChatRoomIdOrderBySentAtDesc(String chatRoomId, Pageable pageable);

    // 특정 시간 이후의 채팅방 메시지 조회
    List<ChatMessage> findByChatRoomIdAndSentAtAfterOrderBySentAtAsc(String chatRoomId, LocalDateTime after);

    // 사용자가 보낸 메시지 조회
    List<ChatMessage> findBySenderId(String senderId);

    // 채팅방의 읽지 않은 메시지 개수 조회
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoomId = :chatRoomId AND cm.senderId != :userId AND cm.read = false")
    long countUnreadMessages(@Param("chatRoomId") String chatRoomId, @Param("userId") String userId);

    // 채팅방의 최근 메시지 조회
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoomId = :chatRoomId ORDER BY cm.sentAt DESC")
    List<ChatMessage> findLatestMessages(@Param("chatRoomId") String chatRoomId, Pageable pageable);

    // 메시지 내용으로 검색
    List<ChatMessage> findByContentContainingIgnoreCaseAndChatRoomId(String content, String chatRoomId);
} 