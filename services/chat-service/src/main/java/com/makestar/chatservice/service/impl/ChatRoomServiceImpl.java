package com.makestar.chatservice.service.impl;

import com.makestar.chatservice.client.UserServiceClient;
import com.makestar.chatservice.dto.ChatMessageDto;
import com.makestar.chatservice.dto.ChatRoomDto;
import com.makestar.chatservice.dto.CreateChatRoomRequest;
import com.makestar.chatservice.model.ChatRoom;
import com.makestar.chatservice.repository.ChatRoomRepository;
import com.makestar.chatservice.service.ChatMessageService;
import com.makestar.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ChatRoomService 인터페이스의 구현 클래스입니다.
 * 채팅방의 생성, 조회, 수정, 삭제 등 채팅방 관련 비즈니스 로직을 처리합니다.
 * 채팅방 참여자 관리와 1:1 채팅방 생성 등의 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    /** 채팅방 정보를 저장하고 조회하는 레포지토리 */
    private final ChatRoomRepository chatRoomRepository;
    
    /** 채팅 메시지 관련 기능을 제공하는 서비스 */
    private final ChatMessageService chatMessageService;
    
    /** 사용자 정보를 조회하기 위한 Feign 클라이언트 */
    private final UserServiceClient userServiceClient;

    /**
     * 채팅방 ID로 채팅방 정보를 조회합니다.
     * 채팅방의 마지막 메시지 정보도 함께 조회합니다.
     *
     * @param chatRoomId 조회할 채팅방 ID
     * @return 채팅방 정보와 마지막 메시지 정보를 담은 DTO
     * @throws EntityNotFoundException 채팅방을 찾을 수 없는 경우
     */
    @Override
    public ChatRoomDto getChatRoomById(String chatRoomId) {
        log.info("Getting chat room by id: {}", chatRoomId);
        
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + chatRoomId));
        
        ChatMessageDto lastMessage = chatMessageService.getLatestMessage(chatRoomId);
        long unreadCount = 0; // 일반 조회에서는 읽지 않은 메시지 수를 0으로 설정
        
        return ChatRoomDto.fromEntityWithLastMessage(chatRoom, lastMessage, unreadCount);
    }

    /**
     * 모든 채팅방 목록을 조회합니다.
     * 각 채팅방의 마지막 메시지 정보도 함께 조회합니다.
     *
     * @return 모든 채팅방 정보 목록
     */
    @Override
    public List<ChatRoomDto> getAllChatRooms() {
        log.info("Getting all chat rooms");
        
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        
        return chatRooms.stream()
                .map(chatRoom -> {
                    ChatMessageDto lastMessage = chatMessageService.getLatestMessage(chatRoom.getId());
                    return ChatRoomDto.fromEntityWithLastMessage(chatRoom, lastMessage, 0);
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 참여중인 모든 채팅방을 조회합니다.
     * 각 채팅방의 마지막 메시지와 읽지 않은 메시지 수를 함께 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자가 참여중인 채팅방 정보 목록
     */
    @Override
    public List<ChatRoomDto> getChatRoomsByUserId(String userId) {
        log.info("Getting chat rooms for user: {}", userId);
        
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByParticipantId(userId);
        
        return chatRooms.stream()
                .map(chatRoom -> {
                    ChatMessageDto lastMessage = chatMessageService.getLatestMessage(chatRoom.getId());
                    long unreadCount = chatMessageService.countUnreadMessages(chatRoom.getId(), userId);
                    return ChatRoomDto.fromEntityWithLastMessage(chatRoom, lastMessage, unreadCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 이름으로 채팅방을 검색합니다.
     * 검색어를 포함하는 모든 채팅방을 조회하며, 대소문자를 구분하지 않습니다.
     *
     * @param name 검색할 채팅방 이름
     * @return 검색된 채팅방 정보 목록
     */
    @Override
    public List<ChatRoomDto> searchChatRoomsByName(String name) {
        log.info("Searching chat rooms by name: {}", name);
        
        List<ChatRoom> chatRooms = chatRoomRepository.findByNameContainingIgnoreCase(name);
        
        return chatRooms.stream()
                .map(chatRoom -> {
                    ChatMessageDto lastMessage = chatMessageService.getLatestMessage(chatRoom.getId());
                    return ChatRoomDto.fromEntityWithLastMessage(chatRoom, lastMessage, 0);
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자를 채팅방에 참여시킵니다.
     *
     * @param chatRoomId 참여할 채팅방 ID
     * @param userId 참여할 사용자 ID
     * @throws EntityNotFoundException 채팅방을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public void joinChatRoom(String chatRoomId, String userId) {
        log.info("User {} joining chat room {}", userId, chatRoomId);
        
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + chatRoomId));
        
        chatRoom.addParticipant(userId);
        chatRoomRepository.save(chatRoom);
    }

    /**
     * 사용자를 채팅방에서 퇴장시킵니다.
     * 마지막 참가자가 퇴장하는 경우 채팅방을 삭제합니다.
     *
     * @param chatRoomId 퇴장할 채팅방 ID
     * @param userId 퇴장할 사용자 ID
     * @throws EntityNotFoundException 채팅방을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public void leaveChatRoom(String chatRoomId, String userId) {
        log.info("User {} leaving chat room {}", userId, chatRoomId);
        
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + chatRoomId));
        
        chatRoom.removeParticipant(userId);
        chatRoomRepository.save(chatRoom);
        
        // 참가자가 없으면 채팅방 삭제
        if (chatRoom.getParticipantIds().isEmpty()) {
            chatRoomRepository.delete(chatRoom);
        }
    }

    /**
     * 두 사용자 간의 1:1 채팅방을 조회하거나 생성합니다.
     * 기존 1:1 채팅방이 있으면 해당 채팅방을 반환하고,
     * 없으면 새로운 1:1 채팅방을 생성합니다.
     *
     * @param userIdA 첫 번째 사용자 ID
     * @param userIdB 두 번째 사용자 ID
     * @return 1:1 채팅방 정보
     */
    @Override
    @Transactional
    public ChatRoomDto getOrCreateDirectChatRoom(String userIdA, String userIdB) {
        log.info("Getting or creating direct chat room between users {} and {}", userIdA, userIdB);
        
        // 기존 1:1 채팅방이 있는지 확인
        List<ChatRoom> directChatRooms = chatRoomRepository.findDirectChatRoomBetweenUsers(userIdA, userIdB);
        
        if (!directChatRooms.isEmpty()) {
            ChatRoom chatRoom = directChatRooms.get(0);
            ChatMessageDto lastMessage = chatMessageService.getLatestMessage(chatRoom.getId());
            long unreadCount = chatMessageService.countUnreadMessages(chatRoom.getId(), userIdA);
            
            return ChatRoomDto.fromEntityWithLastMessage(chatRoom, lastMessage, unreadCount);
        }
        
        // 사용자 정보 조회 (사용자 이름 등을 채팅방 이름으로 사용하기 위해)
        String chatRoomName = "Direct Chat";
        
        try {
            ResponseEntity<Map<String, Object>> userResponse = userServiceClient.getUserById(userIdB);
            if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
                Map<String, Object> userData = userResponse.getBody();
                String username = (String) userData.get("username");
                if (username != null) {
                    chatRoomName = username;
                }
            }
        } catch (Exception e) {
            log.error("Error fetching user info: {}", e.getMessage());
        }
        
        // 새 1:1 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomName)
                .description("Direct chat between users")
                .type(ChatRoom.ChatRoomType.DIRECT)
                .build();
        
        chatRoom.addParticipant(userIdA);
        chatRoom.addParticipant(userIdB);
        
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomDto.fromEntity(savedChatRoom);
    }

    /**
     * 채팅방 정보를 수정합니다.
     *
     * @param chatRoomId 수정할 채팅방 ID
     * @param chatRoomDto 수정할 채팅방 정보를 담은 DTO
     * @return 수정된 채팅방 정보
     * @throws EntityNotFoundException 채팅방을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public ChatRoomDto updateChatRoom(String chatRoomId, ChatRoomDto chatRoomDto) {
        log.info("Updating chat room {}", chatRoomId);
        
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + chatRoomId));
        
        chatRoom.setName(chatRoomDto.getName());
        chatRoom.setDescription(chatRoomDto.getDescription());
        
        ChatRoom updatedChatRoom = chatRoomRepository.save(chatRoom);
        
        ChatMessageDto lastMessage = chatMessageService.getLatestMessage(updatedChatRoom.getId());
        return ChatRoomDto.fromEntityWithLastMessage(updatedChatRoom, lastMessage, 0);
    }

    /**
     * 채팅방을 삭제합니다.
     * 채팅방과 관련된 모든 메시지도 함께 삭제됩니다.
     *
     * @param chatRoomId 삭제할 채팅방 ID
     * @throws EntityNotFoundException 채팅방을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public void deleteChatRoom(String chatRoomId) {
        log.info("Deleting chat room {}", chatRoomId);
        
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + chatRoomId));
        
        chatRoomRepository.delete(chatRoom);
    }

    @Override
    public ChatRoomDto createChatRoom(CreateChatRoomRequest request) {
        // 채팅방 타입 결정
        ChatRoom.ChatRoomType roomType = request.getType() != null && request.getType().equals("DIRECT") 
            ? ChatRoom.ChatRoomType.DIRECT 
            : ChatRoom.ChatRoomType.GROUP;

        // 채팅방 엔티티 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(roomType)
                .creatorId(request.getCreatorId())
                .participantIds(new HashSet<>(request.getParticipantIds()))
                .createdAt(LocalDateTime.now())
                .build();

        // 생성자를 참여자 목록에 추가
        chatRoom.addParticipant(request.getCreatorId());

        // 채팅방 저장
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // 참여자 이름 목록 조회 (사용자 서비스 호출)
        List<String> participantNames = new ArrayList<>();
        try {
            ResponseEntity<List<String>> response = userServiceClient.getUserNames(
                new ArrayList<>(savedChatRoom.getParticipantIds())
            );
            if (response.getBody() != null) {
                participantNames = response.getBody();
            }
        } catch (Exception e) {
            log.error("Failed to fetch participant names", e);
        }

        // DTO 변환 및 반환
        return ChatRoomDto.fromEntityWithParticipants(savedChatRoom, participantNames);
    }

    @Override
    public ChatRoomDto addParticipants(String roomId, Set<String> participantIds) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + roomId));

        // 새로운 참여자 추가
        participantIds.forEach(chatRoom::addParticipant);

        // 채팅방 업데이트
        ChatRoom updatedChatRoom = chatRoomRepository.save(chatRoom);

        // 참여자 이름 목록 조회
        List<String> participantNames = new ArrayList<>();
        try {
            ResponseEntity<List<String>> response = userServiceClient.getUserNames(
                new ArrayList<>(updatedChatRoom.getParticipantIds())
            );
            if (response.getBody() != null) {
                participantNames = response.getBody();
            }
        } catch (Exception e) {
            log.error("Failed to fetch participant names", e);
        }

        // DTO 변환 및 반환
        return ChatRoomDto.fromEntityWithParticipants(updatedChatRoom, participantNames);
    }

    @Override
    public ChatRoomDto removeParticipant(String roomId, String userId) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + roomId));

        // 참여자 제거
        if (!chatRoom.hasParticipant(userId)) {
            throw new IllegalArgumentException("User is not a participant of this chat room");
        }
        chatRoom.removeParticipant(userId);

        // 채팅방이 비어있으면 삭제
        if (chatRoom.getParticipantIds().isEmpty()) {
            chatRoomRepository.delete(chatRoom);
            return null;
        }

        // 채팅방 업데이트
        ChatRoom updatedChatRoom = chatRoomRepository.save(chatRoom);

        // 참여자 이름 목록 조회
        List<String> participantNames = new ArrayList<>();
        try {
            ResponseEntity<List<String>> response = userServiceClient.getUserNames(
                new ArrayList<>(updatedChatRoom.getParticipantIds())
            );
            if (response.getBody() != null) {
                participantNames = response.getBody();
            }
        } catch (Exception e) {
            log.error("Failed to fetch participant names", e);
        }

        // DTO 변환 및 반환
        return ChatRoomDto.fromEntityWithParticipants(updatedChatRoom, participantNames);
    }

    @Override
    public List<ChatRoomDto> getChatRoomsByCreatorId(String creatorId) {
        log.info("Getting chat rooms created by user: {}", creatorId);
        
        List<ChatRoom> chatRooms = chatRoomRepository.findByCreatorId(creatorId);
        
        return chatRooms.stream()
                .map(chatRoom -> {
                    ChatMessageDto lastMessage = chatMessageService.getLatestMessage(chatRoom.getId());
                    long unreadCount = chatMessageService.countUnreadMessages(chatRoom.getId(), creatorId);
                    return ChatRoomDto.fromEntityWithLastMessage(chatRoom, lastMessage, unreadCount);
                })
                .collect(Collectors.toList());
    }
} 