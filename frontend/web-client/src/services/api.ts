import axios from 'axios';
import router from '@/router';

// API 기본 URL 설정
const API_URL = import.meta.env.VITE_API_URL || '/api';

// axios 인스턴스 생성
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 10000 // 10초
});

// 요청 인터셉터 설정
api.interceptors.request.use(
  (config) => {
    // 로컬 스토리지에서 토큰 가져오기
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 설정
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // 인증 에러 처리 (401 Unauthorized)
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      router.push('/login');
    }
    
    // 서버 에러 처리 (500 Internal Server Error)
    if (error.response && error.response.status === 500) {
      console.error('서버 오류가 발생했습니다.');
    }

    return Promise.reject(error);
  }
);

export default api; 