package com.makestar.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.makestar.chat.model.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 채팅 메시지 엔티티에 대한 데이터베이스 접근을 담당하는 리포지토리
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    /**
     * 채팅방의 메시지 목록을 페이징하여 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @param pageable 페이징 정보
     * @return 페이징된 메시지 목록
     */
    Page<ChatMessage> findByChatRoomIdOrderBySentAtDesc(String chatRoomId, Pageable pageable);

    /**
     * 특정 시간 이후의 채팅방 메시지를 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @param since 기준 시간
     * @return 메시지 목록
     */
    List<ChatMessage> findByChatRoomIdAndSentAtAfterOrderBySentAtAsc(String chatRoomId, LocalDateTime since);

    /**
     * 채팅방의 읽지 않은 메시지 개수를 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 읽지 않은 메시지 개수
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoomId = :chatRoomId " +
           "AND m.senderId != :userId AND m.read = false")
    long countUnreadMessages(@Param("chatRoomId") String chatRoomId, @Param("userId") String userId);

    /**
     * 채팅방의 최근 메시지를 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @return 최근 메시지
     */
    Optional<ChatMessage> findFirstByChatRoomIdOrderBySentAtDesc(String chatRoomId);

    /**
     * 메시지 내용으로 메시지를 검색합니다.
     * @param chatRoomId 채팅방 ID
     * @param keyword 검색 키워드
     * @return 검색된 메시지 목록
     */
    List<ChatMessage> findByChatRoomIdAndContentContainingIgnoreCaseOrderBySentAtDesc(String chatRoomId, String keyword);

    /**
     * 특정 사용자가 보낸 메시지를 조회합니다.
     * @param senderId 발신자 ID
     * @param pageable 페이징 정보
     * @return 페이징된 메시지 목록
     */
    Page<ChatMessage> findBySenderIdOrderBySentAtDesc(String senderId, Pageable pageable);

    /**
     * 특정 기간 내의 메시지를 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간의 메시지 목록
     */
    List<ChatMessage> findByChatRoomIdAndSentAtBetweenOrderBySentAtAsc(
            String chatRoomId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 채팅방의 메시지를 모두 삭제합니다.
     * @param chatRoomId 채팅방 ID
     */
    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE m.chatRoomId = :chatRoomId")
    void deleteByChatRoomId(@Param("chatRoomId") String chatRoomId);

    /**
     * 특정 사용자의 메시지를 모두 삭제합니다.
     * @param senderId 발신자 ID
     */
    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE m.senderId = :senderId")
    void deleteBySenderId(@Param("senderId") String senderId);
} 