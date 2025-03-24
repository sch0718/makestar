package com.makestar.chatservice.service.impl;

import com.makestar.chatservice.client.UserServiceClient;
import com.makestar.chatservice.dto.ChatMessageDto;
import com.makestar.chatservice.dto.ChatRoomDto;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public ChatRoomDto createChatRoom(ChatRoomDto chatRoomDto, String creatorId) {
        log.info("Creating chat room: {}, creator: {}", chatRoomDto.getName(), creatorId);
        
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomDto.getName())
                .description(chatRoomDto.getDescription())
                .type(ChatRoom.ChatRoomType.valueOf(chatRoomDto.getType()))
                .build();
        
        // 방 생성자를 참가자로 추가
        chatRoom.addParticipant(creatorId);
        
        // 기존 참가자들 추가
        if (chatRoomDto.getParticipantIds() != null) {
            chatRoomDto.getParticipantIds().forEach(chatRoom::addParticipant);
        }
        
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomDto.fromEntity(savedChatRoom);
    }

    @Override
    public ChatRoomDto getChatRoomById(String chatRoomId) {
        log.info("Getting chat room by id: {}", chatRoomId);
        
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + chatRoomId));
        
        ChatMessageDto lastMessage = chatMessageService.getLatestMessage(chatRoomId);
        long unreadCount = 0; // 일반 조회에서는 읽지 않은 메시지 수를 0으로 설정
        
        return ChatRoomDto.fromEntityWithLastMessage(chatRoom, lastMessage, unreadCount);
    }

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

    @Override
    @Transactional
    public void joinChatRoom(String chatRoomId, String userId) {
        log.info("User {} joining chat room {}", userId, chatRoomId);
        
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + chatRoomId));
        
        chatRoom.addParticipant(userId);
        chatRoomRepository.save(chatRoom);
    }

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

    @Override
    @Transactional
    public void deleteChatRoom(String chatRoomId) {
        log.info("Deleting chat room {}", chatRoomId);
        
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + chatRoomId));
        
        chatRoomRepository.delete(chatRoom);
    }
} 