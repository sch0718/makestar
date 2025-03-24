import api from './api';

// 타입 정의
export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user: {
    id: number;
    username: string;
    email: string;
  };
}

// 인증 서비스
const authService = {
  // 로그인
  async login(data: LoginRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/auth/login', data);
    return response.data;
  },
  
  // 회원가입
  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/auth/register', data);
    return response.data;
  },
  
  // 사용자 정보 가져오기
  async getUserInfo(): Promise<any> {
    const response = await api.get('/auth/me');
    return response.data;
  },
  
  // 로그아웃
  async logout(): Promise<void> {
    await api.post('/auth/logout');
    localStorage.removeItem('token');
  }
};

export default authService; 