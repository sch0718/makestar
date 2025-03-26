package com.makestar.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.makestar.chat.dto.ChatMessageDto;
import com.makestar.chat.service.ChatMessageService;

/**
 * WebSocket을 통한 실시간 채팅 메시지를 처리하는 컨트롤러
 * STOMP 프로토콜을 사용하여 메시지 송수신, 읽음 처리, 채팅방 참여/퇴장 등을 처리합니다.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {

    /** WebSocket 메시지 전송을 위한 템플릿 */
    private final SimpMessagingTemplate messagingTemplate;
    
    /** 채팅 메시지 관련 비즈니스 로직을 처리하는 서비스 */
    private final ChatMessageService chatMessageService;

    /**
     * 채팅방에 새로운 메시지를 전송합니다.
     * 메시지를 저장하고 해당 채팅방의 모든 참여자에게 브로드캐스트합니다.
     *
     * @param roomId 메시지를 전송할 채팅방 ID
     * @param chatMessageDto 전송할 메시지 정보
     */
    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessageDto chatMessageDto) {
        log.info("Received message in room {}: {}", roomId, chatMessageDto.getContent());
        
        // 메시지 저장
        ChatMessageDto savedMessage = chatMessageService.saveMessage(chatMessageDto);
        
        // 채팅방 전체에 메시지 발송
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);
    }

    /**
     * 메시지를 읽음 상태로 표시합니다.
     * 메시지 읽음 처리 후 해당 채팅방의 모든 참여자에게 읽음 상태를 알립니다.
     *
     * @param roomId 채팅방 ID
     * @param messageId 읽음 처리할 메시지 ID
     * @param userId 메시지를 읽은 사용자 ID
     */
    @MessageMapping("/chat.readMessage/{roomId}")
    public void readMessage(@DestinationVariable String roomId, 
                           @Payload String messageId,
                           @Payload String userId) {
        log.info("Reading message {} by user {} in room {}", messageId, userId, roomId);
        
        // 메시지 읽음 처리
        chatMessageService.markAsRead(messageId, userId);
        
        // 해당 채팅방에 메시지가 읽혔음을 알림
        messagingTemplate.convertAndSend("/topic/chat/" + roomId + "/read", messageId);
    }
    
    /**
     * 사용자가 채팅방에 참여했을 때 처리합니다.
     * 시스템 메시지를 생성하여 참여 사실을 채팅방의 모든 참여자에게 알립니다.
     *
     * @param roomId 참여할 채팅방 ID
     * @param userId 참여하는 사용자 ID
     */
    @MessageMapping("/chat.join/{roomId}")
    public void joinRoom(@DestinationVariable String roomId, @Payload String userId) {
        log.info("User {} joined room {}", userId, roomId);
        
        // 시스템 메시지 생성
        ChatMessageDto joinMessage = ChatMessageDto.builder()
                .chatRoomId(roomId)
                .senderId("SYSTEM")
                .type("SYSTEM")
                .content("User " + userId + " joined the room")
                .build();
        
        // 메시지 저장 및 발송
        ChatMessageDto savedMessage = chatMessageService.saveMessage(joinMessage);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);
    }

    /**
     * 사용자가 채팅방을 퇴장했을 때 처리합니다.
     * 시스템 메시지를 생성하여 퇴장 사실을 채팅방의 모든 참여자에게 알립니다.
     *
     * @param roomId 퇴장할 채팅방 ID
     * @param userId 퇴장하는 사용자 ID
     */
    @MessageMapping("/chat.leave/{roomId}")
    public void leaveRoom(@DestinationVariable String roomId, @Payload String userId) {
        log.info("User {} left room {}", userId, roomId);
        
        // 시스템 메시지 생성
        ChatMessageDto leaveMessage = ChatMessageDto.builder()
                .chatRoomId(roomId)
                .senderId("SYSTEM")
                .type("SYSTEM")
                .content("User " + userId + " left the room")
                .build();
        
        // 메시지 저장 및 발송
        ChatMessageDto savedMessage = chatMessageService.saveMessage(leaveMessage);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);
    }

    /**
     * 사용자가 메시지를 입력 중임을 다른 참여자들에게 알립니다.
     *
     * @param roomId 채팅방 ID
     * @param userId 입력 중인 사용자 ID
     */
    @MessageMapping("/chat.typing/{roomId}")
    public void notifyTyping(@DestinationVariable String roomId, @Payload String userId) {
        log.info("User {} is typing in room {}", userId, roomId);
        
        // 타이핑 상태 브로드캐스트
        messagingTemplate.convertAndSend("/topic/chat/" + roomId + "/typing", userId);
    }
} 