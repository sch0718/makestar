import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import io, { Socket } from 'socket.io-client';
import axios from 'axios';
import { useAuthStore } from './auth';

export interface ChatRoom {
  id: number;
  name: string;
  description?: string;
  type: 'DIRECT' | 'GROUP';
  lastMessage?: Message;
  unreadCount: number;
  createdAt: string;
}

export interface Message {
  id: number;
  roomId: number;
  userId: number;
  username: string;
  content: string;
  createdAt: string;
  updatedAt: string;
  isRead: boolean;
}

// Mock 서비스를 대체하기 위한 임시 서비스
export const chatService = {
  getChatRooms: async (): Promise<ChatRoom[]> => {
    const response = await axios.get('/api/chat/rooms');
    return response.data;
  },
  
  getMessages: async (roomId: number): Promise<Message[]> => {
    const response = await axios.get(`/api/chat/rooms/${roomId}/messages`);
    return response.data;
  },
  
  sendMessage: async (roomId: number, content: string): Promise<Message> => {
    const response = await axios.post(`/api/chat/rooms/${roomId}/messages`, { content });
    return response.data;
  },
  
  createChatRoom: async (name: string, participants: string[]): Promise<ChatRoom> => {
    const response = await axios.post('/api/chat/rooms', { name, participants });
    return response.data;
  },
  
  markAsRead: async (roomId: number): Promise<void> => {
    await axios.put(`/api/chat/rooms/${roomId}/read`);
  },
  
  leaveRoom: async (roomId: number): Promise<void> => {
    await axios.delete(`/api/chat/rooms/${roomId}/leave`);
  }
};

export const useChatStore = defineStore('chat', () => {
  // 상태
  const rooms = ref<ChatRoom[]>([]);
  const currentRoomId = ref<number | null>(null);
  const messages = ref<Message[]>([]);
  const socket = ref<Socket | null>(null);
  const typingUsers = ref<Record<number, string[]>>({});
  const isConnected = ref(false);
  const reconnectAttempts = ref(0);
  const maxReconnectAttempts = 5;
  const reconnectInterval = ref<number | null>(null);
  const socketError = ref<string | null>(null);

  // Getter
  const currentRoom = computed(() => {
    if (currentRoomId.value === null) return null;
    return rooms.value.find(room => room.id === currentRoomId.value) || null;
  });

  const sortedRooms = computed(() => {
    return [...rooms.value].sort((a, b) => {
      // 마지막 메시지가 있는 경우 마지막 메시지 시간으로 정렬
      if (a.lastMessage && b.lastMessage) {
        return new Date(b.lastMessage.createdAt).getTime() - new Date(a.lastMessage.createdAt).getTime();
      }
      // 마지막 메시지가 없는 경우 방 생성 시간으로 정렬
      if (!a.lastMessage && !b.lastMessage) {
        return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
      }
      // 마지막 메시지가 있는 방이 위로
      return a.lastMessage ? -1 : 1;
    });
  });

  // 액션
  // 채팅방 목록 로드
  const loadChatRooms = async () => {
    try {
      const response = await chatService.getChatRooms();
      rooms.value = response;
    } catch (error) {
      console.error('채팅방 로드 오류:', error);
      throw error;
    }
  };

  // 특정 채팅방 선택
  const selectRoom = async (roomId: number) => {
    try {
      currentRoomId.value = roomId;
      const loadedMessages = await chatService.getMessages(roomId);
      messages.value = loadedMessages;
      
      // 읽음 처리
      const room = rooms.value.find(r => r.id === roomId);
      if (room && room.unreadCount) {
        room.unreadCount = 0;
        await chatService.markAsRead(roomId);
      }
    } catch (error) {
      console.error('메시지 로드 오류:', error);
      throw error;
    }
  };

  // 새 채팅방 생성
  const createRoom = async (roomData: { name: string; participants: string[] }) => {
    try {
      const newRoom = await chatService.createChatRoom(roomData.name, roomData.participants);
      rooms.value.push(newRoom);
      await selectRoom(newRoom.id);
      return newRoom;
    } catch (error) {
      console.error('채팅방 생성 오류:', error);
      throw error;
    }
  };

  // 메시지 전송
  const sendMessage = async (content: string) => {
    if (!currentRoomId.value) return;
    
    const authStore = useAuthStore();
    const userId = authStore.userId;
    const roomId = currentRoomId.value;
    
    try {
      if (socket.value && isConnected.value) {
        // 소켓으로 메시지 전송
        socket.value.emit('message', { roomId, content });
      } else {
        // HTTP 폴백
        const message = await chatService.sendMessage(roomId, content);
        addMessage(message);
      }
    } catch (error) {
      console.error('메시지 전송 오류:', error);
      socketError.value = '메시지 전송에 실패했습니다. 네트워크 연결을 확인해주세요.';
      // 소켓 연결이 끊겼다면 재연결 시도
      if (!isConnected.value) {
        attemptReconnect();
      }
      throw error;
    }
  };

  // 타이핑 상태 전송
  const sendTypingStatus = (isTyping: boolean) => {
    if (!currentRoomId.value || !socket.value || !isConnected.value) return;
    
    socket.value.emit('typing', {
      roomId: currentRoomId.value,
      isTyping
    });
  };

  // 메시지 추가 (소켓 수신용)
  const addMessage = (message: Message) => {
    messages.value.push(message);
    
    // 현재 보고 있는 방이 아닌 경우 읽지 않은 메시지 카운트 증가
    if (message.roomId !== currentRoomId.value) {
      const room = rooms.value.find(r => r.id === message.roomId);
      if (room) {
        room.unreadCount = (room.unreadCount || 0) + 1;
        room.lastMessage = message;
      }
    }
  };

  // 재연결 시도
  const attemptReconnect = () => {
    // 이미 재연결 시도 중이면 무시
    if (reconnectInterval.value) return;
    
    // 최대 재연결 시도 횟수 초과 확인
    if (reconnectAttempts.value >= maxReconnectAttempts) {
      socketError.value = '연결에 반복적으로 실패했습니다. 페이지를 새로고침하거나 나중에 다시 시도해주세요.';
      return;
    }
    
    reconnectAttempts.value++;
    console.log(`WebSocket 재연결 시도 (${reconnectAttempts.value}/${maxReconnectAttempts})...`);
    
    // 지수 백오프로 재연결 간격 계산 (1초, 2초, 4초, 8초, 16초)
    const delay = Math.min(1000 * Math.pow(2, reconnectAttempts.value - 1), 30000);
    
    reconnectInterval.value = window.setTimeout(() => {
      reconnectInterval.value = null;
      initSocket();
    }, delay);
  };

  // 소켓 연결 초기화
  const initSocket = () => {
    const authStore = useAuthStore();
    
    if (!authStore.token) return;
    
    if (socket.value) {
      socket.value.disconnect();
    }
    
    // 소켓 오류 초기화
    socketError.value = null;
    
    socket.value = io(process.env.VUE_APP_API_URL || 'http://localhost:3000', {
      auth: {
        token: authStore.token
      },
      reconnection: false, // 자체 재연결 로직 사용
    });
    
    // 연결 이벤트
    socket.value.on('connect', () => {
      isConnected.value = true;
      reconnectAttempts.value = 0; // 연결 성공 시 재시도 카운트 초기화
      console.log('웹소켓 연결됨');
    });
    
    // 연결 끊김 이벤트
    socket.value.on('disconnect', () => {
      isConnected.value = false;
      console.log('웹소켓 연결 끊김');
      socketError.value = '서버와의 연결이 끊어졌습니다. 재연결을 시도합니다...';
      attemptReconnect();
    });
    
    // 에러 이벤트
    socket.value.on('connect_error', (error) => {
      isConnected.value = false;
      console.error('웹소켓 연결 오류:', error);
      socketError.value = '서버 연결에 실패했습니다. 재연결을 시도합니다...';
      attemptReconnect();
    });
    
    // 메시지 수신
    socket.value.on('message', (message: Message) => {
      addMessage(message);
    });
    
    // 타이핑 상태 수신
    socket.value.on('typing', ({ roomId, userId, username, isTyping }: any) => {
      if (!typingUsers.value[roomId]) {
        typingUsers.value[roomId] = [];
      }
      
      if (isTyping && !typingUsers.value[roomId].includes(username)) {
        typingUsers.value[roomId].push(username);
      } else if (!isTyping) {
        typingUsers.value[roomId] = typingUsers.value[roomId].filter(user => user !== username);
      }
    });
  };
  
  // 연결 종료
  const disconnect = () => {
    if (socket.value) {
      socket.value.disconnect();
      socket.value = null;
      isConnected.value = false;
    }
    
    // 재연결 타이머가 있으면 중지
    if (reconnectInterval.value) {
      clearTimeout(reconnectInterval.value);
      reconnectInterval.value = null;
    }
    
    // 재연결 카운터 초기화
    reconnectAttempts.value = 0;
    socketError.value = null;
  };

  return {
    // 상태
    rooms,
    currentRoomId,
    messages,
    typingUsers,
    isConnected,
    socketError,
    
    // Getter
    currentRoom,
    sortedRooms,
    
    // 액션
    loadChatRooms,
    selectRoom,
    createRoom,
    sendMessage,
    sendTypingStatus,
    initSocket,
    disconnect,
    attemptReconnect
  };
}); 