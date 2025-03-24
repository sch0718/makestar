import axios from 'axios';

// 타입 정의
export interface ChatRoom {
  id: number;
  name: string;
  description?: string;
  type: 'DIRECT' | 'GROUP';
  lastMessage?: Message;
  unreadCount: number;
  createdAt: string;
}

export interface User {
  id: number;
  username: string;
  displayName?: string;
  avatar?: string;
  status?: 'ONLINE' | 'OFFLINE' | 'AWAY';
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

// 채팅 API 서비스
export const chatService = {
  // 채팅방 목록 조회
  getChatRooms: async (): Promise<ChatRoom[]> => {
    const response = await axios.get('/api/chat/rooms');
    return response.data;
  },
  
  // 특정 채팅방 조회
  getChatRoom: async (roomId: number): Promise<ChatRoom> => {
    const response = await axios.get(`/api/chat/rooms/${roomId}`);
    return response.data;
  },
  
  // 채팅방 메시지 조회
  getMessages: async (roomId: number): Promise<Message[]> => {
    const response = await axios.get(`/api/chat/rooms/${roomId}/messages`);
    return response.data;
  },
  
  // 메시지 전송
  sendMessage: async (roomId: number, content: string): Promise<Message> => {
    const response = await axios.post(`/api/chat/rooms/${roomId}/messages`, { content });
    return response.data;
  },
  
  // 메시지 읽음 처리
  markAsRead: async (roomId: number): Promise<void> => {
    await axios.put(`/api/chat/rooms/${roomId}/read`);
  },
  
  // 새 채팅방 생성
  createChatRoom: async (name: string, participants: string[]): Promise<ChatRoom> => {
    const response = await axios.post('/api/chat/rooms', { name, participants });
    return response.data;
  },
  
  // 채팅방 나가기
  leaveRoom: async (roomId: number): Promise<void> => {
    await axios.delete(`/api/chat/rooms/${roomId}/leave`);
  },
  
  // 채팅방 초대
  inviteToRoom: async (roomId: number, usernames: string[]): Promise<void> => {
    await axios.post(`/api/chat/rooms/${roomId}/invite`, { usernames });
  },
  
  // 사용자 검색
  searchUsers: async (query: string): Promise<User[]> => {
    const response = await axios.get('/api/users/search', { params: { query } });
    return response.data;
  }
}; 