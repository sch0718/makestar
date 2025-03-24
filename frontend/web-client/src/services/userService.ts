import api from './api';

// 타입 정의
export interface User {
  id: number;
  username: string;
  email: string;
  status: 'ONLINE' | 'OFFLINE' | 'AWAY';
  profileImageUrl?: string;
}

export interface UpdateProfileRequest {
  status?: 'ONLINE' | 'OFFLINE' | 'AWAY';
  profileImageUrl?: string;
}

export interface FriendRequest {
  id: number;
  senderId: number;
  receiverId: number;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  createdAt: string;
}

// 사용자 서비스
const userService = {
  // 사용자 검색
  async searchUsers(query: string): Promise<User[]> {
    const response = await api.get<User[]>(`/users/search?query=${query}`);
    return response.data;
  },
  
  // 사용자 프로필 조회
  async getUserProfile(userId: number): Promise<User> {
    const response = await api.get<User>(`/users/${userId}`);
    return response.data;
  },
  
  // 프로필 업데이트
  async updateProfile(data: UpdateProfileRequest): Promise<User> {
    const response = await api.put<User>('/users/profile', data);
    return response.data;
  },
  
  // 친구 목록 조회
  async getFriends(): Promise<User[]> {
    const response = await api.get<User[]>('/users/friends');
    return response.data;
  },
  
  // 친구 요청 목록 조회
  async getFriendRequests(): Promise<FriendRequest[]> {
    const response = await api.get<FriendRequest[]>('/users/friend-requests');
    return response.data;
  },
  
  // 친구 요청 보내기
  async sendFriendRequest(userId: number): Promise<FriendRequest> {
    const response = await api.post<FriendRequest>(`/users/friend-requests`, { receiverId: userId });
    return response.data;
  },
  
  // 친구 요청 수락
  async acceptFriendRequest(requestId: number): Promise<void> {
    await api.put(`/users/friend-requests/${requestId}/accept`);
  },
  
  // 친구 요청 거절
  async rejectFriendRequest(requestId: number): Promise<void> {
    await api.put(`/users/friend-requests/${requestId}/reject`);
  }
};

export default userService; 