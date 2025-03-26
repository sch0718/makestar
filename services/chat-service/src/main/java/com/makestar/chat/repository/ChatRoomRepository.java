package com.makestar.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.makestar.chat.model.ChatRoom;
import com.makestar.chat.model.ChatRoom.ChatRoomType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 채팅방 엔티티에 대한 데이터베이스 접근을 담당하는 리포지토리
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    /**
     * 채팅방 이름에 특정 문자열이 포함된 채팅방을 검색합니다.
     * @param name 검색할 채팅방 이름
     * @return 검색된 채팅방 목록
     */
    List<ChatRoom> findByNameContainingIgnoreCase(String name);

    /**
     * 특정 사용자가 생성한 채팅방 목록을 조회합니다.
     * @param creatorId 생성자 ID
     * @return 생성한 채팅방 목록
     */
    List<ChatRoom> findByCreatorId(String creatorId);

    /**
     * 특정 사용자가 참여한 채팅방 목록을 조회합니다.
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 페이징된 채팅방 목록
     */
    @Query("SELECT r FROM ChatRoom r WHERE :userId MEMBER OF r.participantIds")
    Page<ChatRoom> findByParticipantId(String userId, Pageable pageable);

    /**
     * 두 사용자 간의 1:1 채팅방을 조회합니다.
     * @param userId1 첫 번째 사용자 ID
     * @param userId2 두 번째 사용자 ID
     * @return 1:1 채팅방 (존재하는 경우)
     */
    @Query("SELECT r FROM ChatRoom r WHERE r.type = com.makestar.chat.model.ChatRoom$ChatRoomType.DIRECT " +
           "AND :userId1 MEMBER OF r.participantIds " +
           "AND :userId2 MEMBER OF r.participantIds")
    Optional<ChatRoom> findDirectChatRoom(@Param("userId1") String userId1, @Param("userId2") String userId2);

    /**
     * 채팅방의 참여자 수를 조회합니다.
     * @param chatRoomId 채팅방 ID
     * @return 참여자 수
     */
    @Query("SELECT SIZE(r.participantIds) FROM ChatRoom r WHERE r.id = :chatRoomId")
    long countParticipants(@Param("chatRoomId") String chatRoomId);

    /**
     * 특정 사용자가 참여한 채팅방 수를 조회합니다.
     * @param userId 사용자 ID
     * @return 참여한 채팅방 수
     */
    @Query("SELECT COUNT(r) FROM ChatRoom r WHERE :userId MEMBER OF r.participantIds")
    long countByParticipantId(@Param("userId") String userId);

    /**
     * 특정 기간 내에 생성된 채팅방을 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간에 생성된 채팅방 목록
     */
    List<ChatRoom> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 채팅방 유형별로 채팅방을 조회합니다.
     * @param type 채팅방 유형
     * @param pageable 페이징 정보
     * @return 페이징된 채팅방 목록
     */
    Page<ChatRoom> findByType(ChatRoomType type, Pageable pageable);

    /**
     * 특정 사용자가 참여중인 모든 채팅방을 조회합니다.
     * @param userId 사용자 ID
     * @return 사용자가 참여중인 채팅방 목록
     */
    @Query("SELECT r FROM ChatRoom r JOIN r.participantIds pid WHERE pid = :userId")
    List<ChatRoom> findChatRoomsByParticipantId(@Param("userId") String userId);

    /**
     * 두 사용자 간의 1:1 채팅방 목록을 조회합니다.
     * @param userIdA 첫 번째 사용자 ID
     * @param userIdB 두 번째 사용자 ID
     * @return 두 사용자 간의 1:1 채팅방 목록
     */
    @Query("SELECT r FROM ChatRoom r WHERE r.type = com.makestar.chat.model.ChatRoom$ChatRoomType.DIRECT " +
           "AND :userIdA MEMBER OF r.participantIds " +
           "AND :userIdB MEMBER OF r.participantIds")
    List<ChatRoom> findDirectChatRoomBetweenUsers(@Param("userIdA") String userIdA, @Param("userIdB") String userIdB);

    /**
     * 특정 참여자가 포함된 채팅방을 검색합니다.
     * @param participantId 참여자 ID
     * @return 참여자가 포함된 채팅방 목록
     */
    @Query("SELECT r FROM ChatRoom r WHERE :participantId MEMBER OF r.participantIds")
    List<ChatRoom> findByParticipantIdsContaining(@Param("participantId") String participantId);

    /**
     * 특정 유형과 참여자들이 모두 포함된 채팅방을 검색합니다.
     * @param type 채팅방 유형
     * @param participantIds 참여자 ID 목록
     * @param participantCount 참여자 수
     * @return 조건에 맞는 채팅방 목록
     */
    @Query("SELECT r FROM ChatRoom r WHERE r.type = :type " +
           "AND (SELECT COUNT(DISTINCT pid) FROM r.participantIds pid WHERE pid IN :participantIds) = :participantCount")
    List<ChatRoom> findByTypeAndParticipantIdsContainingAll(
            @Param("type") ChatRoomType type,
            @Param("participantIds") List<String> participantIds,
            @Param("participantCount") long participantCount);
} 