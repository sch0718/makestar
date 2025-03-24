package com.makestar.chatservice.service;

import com.makestar.chatservice.dto.ChatRoomDto;

import java.util.List;

public interface ChatRoomService {
    
    // 채팅방 생성
    ChatRoomDto createChatRoom(ChatRoomDto chatRoomDto, String creatorId);
    
    // 채팅방 조회
    ChatRoomDto getChatRoomById(String chatRoomId);
    
    // 채팅방 목록 조회
    List<ChatRoomDto> getAllChatRooms();
    
    // 사용자가 참여한 채팅방 목록 조회
    List<ChatRoomDto> getChatRoomsByUserId(String userId);
    
    // 채팅방 이름으로 검색
    List<ChatRoomDto> searchChatRoomsByName(String name);
    
    // 채팅방 참여
    void joinChatRoom(String chatRoomId, String userId);
    
    // 채팅방 나가기
    void leaveChatRoom(String chatRoomId, String userId);
    
    // 1:1 채팅방 생성 또는 조회
    ChatRoomDto getOrCreateDirectChatRoom(String userIdA, String userIdB);
    
    // 채팅방 업데이트
    ChatRoomDto updateChatRoom(String chatRoomId, ChatRoomDto chatRoomDto);
    
    // 채팅방 삭제
    void deleteChatRoom(String chatRoomId);
} 