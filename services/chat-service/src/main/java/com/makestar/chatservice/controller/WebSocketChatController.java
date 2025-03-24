package com.makestar.chatservice.controller;

import com.makestar.chatservice.dto.ChatMessageDto;
import com.makestar.chatservice.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    // 채팅방 메시지 전송
    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessageDto chatMessageDto) {
        log.info("Received message in room {}: {}", roomId, chatMessageDto.getContent());
        
        // 메시지 저장
        ChatMessageDto savedMessage = chatMessageService.saveMessage(chatMessageDto);
        
        // 채팅방 전체에 메시지 발송
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);
    }

    // 메시지 읽음 처리
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
    
    // 채팅방 참여
    @MessageMapping("/chat.join/{roomId}")
    public void joinRoom(@DestinationVariable String roomId, @Payload String userId) {
        log.info("User {} joined room {}", userId, roomId);
        
        // 시스템 메시지 생성
        ChatMessageDto systemMessage = ChatMessageDto.builder()
                .chatRoomId(roomId)
                .senderId("SYSTEM")
                .content(userId + " 님이 채팅방에 참여했습니다.")
                .type("SYSTEM")
                .build();
        
        // 메시지 저장
        ChatMessageDto savedMessage = chatMessageService.saveMessage(systemMessage);
        
        // 채팅방 전체에 메시지 발송
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);
    }
    
    // 채팅방 나가기
    @MessageMapping("/chat.leave/{roomId}")
    public void leaveRoom(@DestinationVariable String roomId, @Payload String userId) {
        log.info("User {} left room {}", userId, roomId);
        
        // 시스템 메시지 생성
        ChatMessageDto systemMessage = ChatMessageDto.builder()
                .chatRoomId(roomId)
                .senderId("SYSTEM")
                .content(userId + " 님이 채팅방을 나갔습니다.")
                .type("SYSTEM")
                .build();
        
        // 메시지 저장
        ChatMessageDto savedMessage = chatMessageService.saveMessage(systemMessage);
        
        // 채팅방 전체에 메시지 발송
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);
    }
    
    // 타이핑 중 알림
    @MessageMapping("/chat.typing/{roomId}")
    public void typing(@DestinationVariable String roomId, @Payload String userId) {
        log.info("User {} is typing in room {}", userId, roomId);
        
        // 채팅방 전체에 타이핑 중 알림 발송
        messagingTemplate.convertAndSend("/topic/chat/" + roomId + "/typing", userId);
    }
} 