import { defineStore } from 'pinia';
import { ref, computed, watch } from 'vue';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import axios from 'axios';
import { useAuthStore } from './auth';
import { useRouter } from 'vue-router';

export interface ChatRoom {
  id: number;
  name: string;
  lastMessage?: string;
  lastMessageTime?: string;
  unreadCount: number;
  createdAt?: string;
}

export interface Message {
  id: number;
  senderId: number;
  senderName: string;
  content: string;
  timestamp: string;
  roomId: number;
}

/**
 * 채팅 관련 상태 및 기능을 관리하는 스토어입니다.
 * WebSocket 연결, 메시지 송수신, 채팅방 관리 등의 기능을 제공합니다.
 */
export const useChatStore = defineStore('chat', () => {
  // State
  const rooms = ref<ChatRoom[]>([]);
  const currentRoomId = ref<number | null>(null);
  const messages = ref<Message[]>([]);
  const stompClient = ref<Client | null>(null);
  const typingUsers = ref<Record<number, string[]>>({});
  const isConnected = ref(false);
  const isConnecting = ref(false);
  const connectionError = ref<string | null>(null);
  const reconnectAttempts = ref(0);
  const maxReconnectAttempts = 5;
  const socketError = ref<string | null>(null);
  const connecting = ref(false);
  const connected = ref(false);
  const socket = ref<WebSocket | null>(null);
  
  // 서버의 기본 URL을 환경에 따라 결정합니다
  const getBaseUrl = () => {
    // 개발 환경에서는 localhost 사용, 그 외에는 현재 호스트 사용
    return (import.meta.env as any)?.DEV 
      ? 'http://localhost:8080'
      : window.location.origin;
  };

  // Getter
  const currentRoom = computed(() => 
    rooms.value.find(room => room.id === currentRoomId.value) || null
  );

  // 채팅방 목록을 마지막 메시지 시간 기준으로 정렬
  const sortedRooms = computed(() => {
    return [...rooms.value].sort((a, b) => {
      // 마지막 메시지가 없는 경우 생성일 기준으로 정렬
      const timeA = a.lastMessageTime || a.createdAt || '';
      const timeB = b.lastMessageTime || b.createdAt || '';
      // 최신 메시지가 위로 오도록 내림차순 정렬
      return new Date(timeB).getTime() - new Date(timeA).getTime();
    });
  });

  // 특정 방의 메시지 목록 반환
  const getRoomMessages = computed(() => 
    messages.value.filter(msg => msg.roomId === currentRoomId.value)
  );

  // 방 목록 불러오기
  const fetchRooms = async () => {
    try {
      const response = await axios.get('/api/chat/rooms');
      rooms.value = response.data;
      return response.data;
    } catch (error) {
      console.error('Error fetching chat rooms:', error);
      throw error;
    }
  };

  // 특정 방의 메시지 불러오기
  const fetchMessages = async (roomId: number) => {
    try {
      const response = await axios.get(`/api/chat/rooms/${roomId}/messages`);
      // 방에 해당하는 메시지만 필터링하여 상태에 추가
      const roomMessages = response.data;
      
      // 기존 메시지 중 현재 방이 아닌 메시지들만 유지
      const otherMessages = messages.value.filter(msg => msg.roomId !== roomId);
      
      // 새로운 메시지와 기존 다른 방 메시지를 합침
      messages.value = [...otherMessages, ...roomMessages];
      
      return roomMessages;
    } catch (error) {
      console.error(`Error fetching messages for room ${roomId}:`, error);
      throw error;
    }
  };

  // 방 선택
  const selectRoom = async (roomId: number) => {
    currentRoomId.value = roomId;
    await fetchMessages(roomId);
    // 읽지 않은 메시지 카운트 초기화
    const room = rooms.value.find(r => r.id === roomId);
    if (room) {
      room.unreadCount = 0;
    }
  };

  // 메시지 추가
  const addMessage = (message: Message) => {
    messages.value.push(message);
    
    // 해당 방의 lastMessage와 시간 업데이트
    const room = rooms.value.find(r => r.id === message.roomId);
    if (room) {
      room.lastMessage = message.content;
      room.lastMessageTime = message.timestamp;
      
      // 현재 선택된 방이 아니라면 안 읽은 메시지 카운트 증가
      if (currentRoomId.value !== message.roomId) {
        room.unreadCount += 1;
      }
    }
  };

  // 메시지 전송
  const sendMessage = async (content: string) => {
    if (!currentRoomId.value) return;
    
    const authStore = useAuthStore();
    const userId = authStore.userId;
    const roomId = currentRoomId.value;
    
    try {
      if (stompClient.value && stompClient.value.connected) {
        // STOMP로 메시지 전송
        stompClient.value.publish({
          destination: `/app/chat/${roomId}/send`,
          body: JSON.stringify({ content }),
          headers: {
            'content-type': 'application/json'
          }
        });
        return true;
      } else {
        // HTTP 폴백
        const response = await axios.post(`/api/chat/rooms/${roomId}/messages`, { content });
        const message = response.data;
        addMessage(message);
        return true;
      }
    } catch (error) {
      console.error('메시지 전송 오류:', error);
      socketError.value = '메시지 전송에 실패했습니다. 네트워크 연결을 확인해주세요.';
      // 소켓 연결이 끊겼다면 재연결 시도
      if (!isConnected.value) {
        attemptReconnect();
      }
      return false;
    }
  };

  // 웹소켓 메시지 전송 (일반 목적용)
  const sendWebSocketMessage = (destination: string, body: any) => {
    if (!stompClient.value?.connected) {
      console.debug('Cannot send message: not connected');
      initializeWebSocketConnection();
      return false;
    }

    try {
      console.debug(`Sending message to ${destination}`, body);
      stompClient.value.publish({
        destination,
        body: JSON.stringify(body),
        headers: {
          'content-type': 'application/json'
        }
      });
      return true;
    } catch (error) {
      console.error('Failed to send message', error);
      return false;
    }
  };

  // 타이핑 중 상태 전송
  const sendTypingStatus = (isTyping: boolean) => {
    if (!currentRoomId.value) return;
    
    const roomId = currentRoomId.value;
    const authStore = useAuthStore();
    
    if (authStore.user?.id) {
      stompClient.value?.publish({
        destination: `/app/chat/${roomId}/typing`,
        body: JSON.stringify({ isTyping })
      });
    }
  };
  
  // 소켓 연결 재시도
  const attemptReconnect = () => {
    if (reconnectAttempts.value >= maxReconnectAttempts) {
      socketError.value = '연결 시도 횟수를 초과했습니다. 페이지를 새로고침해주세요.';
      return;
    }
    
    reconnectAttempts.value += 1;
    setTimeout(() => {
      initializeWebSocketConnection();
    }, Math.min(1000 * Math.pow(2, reconnectAttempts.value), 30000)); // 지수 백오프 (최대 30초)
  };
  
  // WebSocket 초기화 (기존 방식)
  const initSocket = () => {
    if (connecting.value) return;
    connecting.value = true;
    
    try {
      // 인증 스토어에서 토큰 가져오기
      const authStore = useAuthStore();
      const token = authStore.token || localStorage.getItem('token');
      
      if (!token) {
        console.error('Authentication token not available');
        socketError.value = '인증 정보가 없습니다.';
        connecting.value = false;
        return;
      }
      
      // 웹소켓 연결
      const wsUrl = `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}/ws?token=${token}`;
      const newSocket = new WebSocket(wsUrl);
      
      newSocket.onopen = () => {
        socket.value = newSocket;
        connected.value = true;
        connecting.value = false;
        socketError.value = null;
      };
      
      newSocket.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === 'CHAT') {
            addMessage(data.message);
          } else if (data.type === 'TYPING') {
            handleTypingEvent(data);
          }
        } catch (error) {
          console.error('Error parsing WebSocket message:', error);
        }
      };
      
      newSocket.onclose = () => {
        socket.value = null;
        connected.value = false;
        connecting.value = false;
        // 자동 재연결 시도
        setTimeout(initSocket, 3000);
      };
      
      newSocket.onerror = (error) => {
        console.error('WebSocket error:', error);
        socket.value = null;
        connected.value = false;
        connecting.value = false;
        socketError.value = '연결 오류가 발생했습니다.';
      };
    } catch (error) {
      console.error('Failed to initialize WebSocket:', error);
      connecting.value = false;
      socketError.value = '연결 초기화에 실패했습니다.';
    }
  };
  
  // WebSocket 연결 종료 (기존 방식)
  const disconnectSocket = () => {
    if (socket.value) {
      socket.value.close();
      socket.value = null;
      connected.value = false;
    }
  };

  /**
   * WebSocket 연결을 초기화합니다.
   * SockJS를 통해 서버와 연결하고 STOMP 클라이언트를 설정합니다.
   */
  const initializeWebSocketConnection = () => {
    try {
      isConnecting.value = true;
      connectionError.value = null;

      if (stompClient.value?.connected) {
        console.debug('Already connected to WebSocket');
        isConnected.value = true;
        isConnecting.value = false;
        return;
      }

      // 인증 토큰 가져오기
      const authStore = useAuthStore();
      const token = authStore.token;
      if (!token) {
        console.debug('No authentication token available');
        connectionError.value = '인증 정보가 없습니다.';
        isConnecting.value = false;
        return;
      }

      const baseUrl = getBaseUrl();
      const wsUrl = `${baseUrl}/api/chat-ws`;
      console.debug(`Connecting to WebSocket at ${wsUrl}`);

      // SockJS 연결 생성 (withCredentials 옵션 추가)
      const socket = new SockJS(wsUrl, null, {
        transports: ['websocket', 'xhr-streaming', 'xhr-polling'],
        timeout: 10000,
        withCredentials: true
      });

      // STOMP 클라이언트 생성 및 설정
      const client = new Client({
        webSocketFactory: () => socket,
        debug: (str) => {
          // 개발 환경에서만 디버그 메시지 출력
          if ((import.meta.env as any)?.DEV) {
            console.debug(str);
          }
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        connectHeaders: {
          'X-Authorization': `Bearer ${token}`
        },
        onConnect: () => {
          console.debug('Connected to WebSocket');
          isConnected.value = true;
          isConnecting.value = false;
          subscribeToPersonalMessages();
        },
        onStompError: (frame) => {
          console.error('STOMP error', frame);
          connectionError.value = `STOMP error: ${frame.headers['message']}`;
          isConnecting.value = false;
          handleDisconnection();
        },
        onWebSocketClose: (event) => {
          console.debug('WebSocket connection closed', event);
          isConnected.value = false;
          if (event.code !== 1000) {
            connectionError.value = `연결이 종료되었습니다: ${event.reason || '알 수 없는 이유'}`;
            handleDisconnection();
          }
        },
        onWebSocketError: (event) => {
          console.error('WebSocket error', event);
          connectionError.value = '웹소켓 연결 오류가 발생했습니다.';
          isConnecting.value = false;
          handleDisconnection();
        }
      });

      stompClient.value = client;
      client.activate();
    } catch (error) {
      console.error('Failed to initialize WebSocket connection', error);
      connectionError.value = `연결 초기화 실패: ${error instanceof Error ? error.message : '알 수 없는 오류'}`;
      isConnecting.value = false;
      handleDisconnection();
    }
  };

  /**
   * 개인 메시지를 구독합니다.
   * 사용자별 구독 경로를 설정하여 개인 메시지를 수신합니다.
   */
  const subscribeToPersonalMessages = () => {
    const authStore = useAuthStore();
    if (!stompClient.value?.connected || !authStore.user?.id) {
      console.debug('Cannot subscribe: not connected or no user ID');
      return;
    }

    const userId = authStore.user.id;
    const personalMessagesTopic = `/user/${userId}/queue/messages`;
    
    console.debug(`Subscribing to personal messages at ${personalMessagesTopic}`);
    
    stompClient.value.subscribe(personalMessagesTopic, (message) => {
      try {
        const receivedMessage = JSON.parse(message.body);
        console.debug('Received message:', receivedMessage);
        addMessage(receivedMessage);
      } catch (error) {
        console.error('Error parsing received message', error);
      }
    });
  };

  /**
   * 연결 해제 처리를 담당합니다.
   * 연결 오류 시 인증 상태를 확인하고 필요시 재로그인을 유도합니다.
   */
  const handleDisconnection = () => {
    isConnected.value = false;
    
    // 인증 관련 오류인 경우 재로그인 유도
    if (connectionError.value?.includes('인증') || connectionError.value?.includes('권한')) {
      const authStore = useAuthStore();
      authStore.logout(); // clearAuth 대신 사용 가능한 logout 메소드
      const router = useRouter();
      router.push('/login');
    }
  };

  // 웹소켓 연결 종료 (STOMP/SockJS 방식)
  const disconnect = () => {
    if (stompClient.value?.connected) {
      console.debug('Disconnecting from WebSocket');
      stompClient.value.deactivate();
    }
    isConnected.value = false;
  };
  
  // 타이핑 이벤트 처리
  const handleTypingEvent = (data: any) => {
    const { roomId, userId, username, isTyping } = data;
    
    if (!typingUsers.value[roomId]) {
      typingUsers.value[roomId] = [];
    }
    
    if (isTyping) {
      // 이미 타이핑 목록에 없는 경우에만 추가
      if (!typingUsers.value[roomId].includes(username)) {
        typingUsers.value[roomId].push(username);
      }
    } else {
      // 타이핑 중지한 사용자 제거
      typingUsers.value[roomId] = typingUsers.value[roomId].filter(name => name !== username);
    }
  };

  // 인증 상태 변경 감시
  watch(() => {
    const authStore = useAuthStore();
    return authStore.token; // isAuthenticated 대신 token 사용
  }, (token) => {
    if (token) {
      console.debug('Authentication state changed: authenticated');
      initializeWebSocketConnection();
    } else {
      console.debug('Authentication state changed: not authenticated');
      disconnectSocket();
      disconnect();
    }
  }, { immediate: true });

  return {
    rooms,
    currentRoomId,
    currentRoom,
    messages,
    getRoomMessages,
    typingUsers,
    isConnected,
    isConnecting,
    connectionError,
    socketError,
    connecting,
    connected,
    fetchRooms,
    fetchMessages,
    selectRoom,
    addMessage,
    sendMessage,
    sendTypingStatus,
    initSocket,
    disconnectSocket,
    attemptReconnect,
    initializeWebSocketConnection,
    disconnect,
    subscribeToPersonalMessages,
    sendWebSocketMessage,
    sortedRooms
  };
}); 