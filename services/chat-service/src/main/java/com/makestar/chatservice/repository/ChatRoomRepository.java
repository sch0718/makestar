package com.makestar.chatservice.repository;

import com.makestar.chatservice.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    // 사용자가 참여한 채팅방 목록 조회
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participantIds pid WHERE pid = :userId")
    List<ChatRoom> findChatRoomsByParticipantId(@Param("userId") String userId);

    // 1:1 채팅방 찾기 (두 사용자 간)
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.type = com.makestar.chatservice.model.ChatRoom$ChatRoomType.DIRECT " +
           "AND SIZE(cr.participantIds) = 2 " +
           "AND :userIdA MEMBER OF cr.participantIds " +
           "AND :userIdB MEMBER OF cr.participantIds")
    List<ChatRoom> findDirectChatRoomBetweenUsers(@Param("userIdA") String userIdA, @Param("userIdB") String userIdB);

    // 채팅방 이름으로 검색
    List<ChatRoom> findByNameContainingIgnoreCase(String name);
} 