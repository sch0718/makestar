import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import axios from 'axios';

interface User {
  id: number;
  username: string;
  email: string;
  status: 'ONLINE' | 'OFFLINE' | 'AWAY';
  profileImageUrl?: string;
}

export const useAuthStore = defineStore('auth', () => {
  // 상태
  const token = ref<string | null>(localStorage.getItem('token'));
  const user = ref<User | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  // 게터
  const isLoggedIn = computed(() => !!token.value);
  const userId = computed(() => user.value?.id);

  // 액션
  const login = async (username: string, password: string) => {
    loading.value = true;
    error.value = null;

    try {
      const response = await axios.post('/api/auth/login', { username, password });
      token.value = response.data.token;
      user.value = response.data;
      
      // 토큰 저장
      localStorage.setItem('token', response.data.token);
      
      // 인증 헤더 설정
      axios.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
      
      return response.data;
    } catch (err: any) {
      error.value = err.response?.data?.message || '로그인에 실패했습니다';
      throw err;
    } finally {
      loading.value = false;
    }
  };

  const register = async (userData: { username: string; email: string; password: string }) => {
    loading.value = true;
    error.value = null;

    try {
      const response = await axios.post('/api/auth/register', userData);
      token.value = response.data.token;
      user.value = response.data;
      
      // 토큰 저장
      localStorage.setItem('token', response.data.token);
      
      // 인증 헤더 설정
      axios.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
      
      return response.data;
    } catch (err: any) {
      error.value = err.response?.data?.message || '회원가입에 실패했습니다';
      throw err;
    } finally {
      loading.value = false;
    }
  };

  const logout = () => {
    token.value = null;
    user.value = null;
    localStorage.removeItem('token');
    delete axios.defaults.headers.common['Authorization'];
  };

  const fetchCurrentUser = async () => {
    if (!token.value) return null;
    
    loading.value = true;
    
    try {
      const response = await axios.get('/api/auth/me');
      user.value = response.data;
      return response.data;
    } catch (err: any) {
      error.value = err.response?.data?.message || '사용자 정보를 가져오는데 실패했습니다';
      
      // 인증 오류인 경우 로그아웃 처리
      if (err.response?.status === 401) {
        logout();
      }
      
      throw err;
    } finally {
      loading.value = false;
    }
  };

  return {
    // 상태
    token,
    user,
    loading,
    error,
    
    // 게터
    isLoggedIn,
    userId,
    
    // 액션
    login,
    register,
    logout,
    fetchCurrentUser
  };
}); 