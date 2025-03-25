package com.makestar.chatservice.service;

import com.makestar.chatservice.dto.ChatRoomDto;
import com.makestar.chatservice.dto.CreateChatRoomRequest;
import java.util.List;
import java.util.Set;

/**
 * 채팅방 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface ChatRoomService {
    /**
     * 새로운 채팅방을 생성합니다.
     *
     * @param request 채팅방 생성 요청 정보
     * @return 생성된 채팅방 정보
     */
    ChatRoomDto createChatRoom(CreateChatRoomRequest request);

    /**
     * 특정 사용자가 참여한 모든 채팅방을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 채팅방 목록
     */
    List<ChatRoomDto> getChatRoomsByUserId(String userId);

    /**
     * 특정 사용자가 생성한 모든 채팅방을 조회합니다.
     *
     * @param creatorId 생성자 ID
     * @return 채팅방 목록
     */
    List<ChatRoomDto> getChatRoomsByCreatorId(String creatorId);

    /**
     * 채팅방 ID로 특정 채팅방을 조회합니다.
     *
     * @param roomId 채팅방 ID
     * @return 채팅방 정보
     */
    ChatRoomDto getChatRoomById(String roomId);

    /**
     * 채팅방에 새로운 참여자를 추가합니다.
     *
     * @param roomId 채팅방 ID
     * @param participantIds 추가할 참여자 ID 목록
     * @return 업데이트된 채팅방 정보
     */
    ChatRoomDto addParticipants(String roomId, Set<String> participantIds);

    /**
     * 채팅방에서 참여자를 제거합니다.
     *
     * @param roomId 채팅방 ID
     * @param userId 제거할 사용자 ID
     * @return 업데이트된 채팅방 정보
     */
    ChatRoomDto removeParticipant(String roomId, String userId);

    /**
     * 모든 채팅방 목록을 조회합니다.
     * @return 채팅방 목록
     */
    List<ChatRoomDto> getAllChatRooms();
    
    /**
     * 채팅방 이름으로 채팅방을 검색합니다.
     * @param name 검색할 채팅방 이름
     * @return 검색된 채팅방 목록
     */
    List<ChatRoomDto> searchChatRoomsByName(String name);
    
    /**
     * 사용자를 채팅방에 참여시킵니다.
     * @param chatRoomId 채팅방 ID
     * @param userId 참여할 사용자 ID
     */
    void joinChatRoom(String chatRoomId, String userId);
    
    /**
     * 사용자를 채팅방에서 내보냅니다.
     * @param chatRoomId 채팅방 ID
     * @param userId 나갈 사용자 ID
     */
    void leaveChatRoom(String chatRoomId, String userId);
    
    /**
     * 1:1 채팅방을 생성하거나 이미 존재하는 경우 해당 채팅방을 반환합니다.
     * @param userIdA 첫 번째 사용자 ID
     * @param userIdB 두 번째 사용자 ID
     * @return 1:1 채팅방 정보
     */
    ChatRoomDto getOrCreateDirectChatRoom(String userIdA, String userIdB);
    
    /**
     * 채팅방 정보를 업데이트합니다.
     * @param chatRoomId 채팅방 ID
     * @param chatRoomDto 업데이트할 채팅방 정보
     * @return 업데이트된 채팅방 정보
     */
    ChatRoomDto updateChatRoom(String chatRoomId, ChatRoomDto chatRoomDto);
    
    /**
     * 채팅방을 삭제합니다.
     * @param chatRoomId 삭제할 채팅방 ID
     */
    void deleteChatRoom(String chatRoomId);
} 