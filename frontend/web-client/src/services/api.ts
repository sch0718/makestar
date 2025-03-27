import axios from 'axios';
import router from '@/router';

// API 클라이언트 생성
const api = axios.create({
  baseURL: '/api', // 프록시 경로 사용
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  withCredentials: true, // CORS 요청에 쿠키 포함
});

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    // 로컬 스토리지에서 토큰 가져오기
    const token = localStorage.getItem('token');
    
    // 인증 헤더에 토큰 추가
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API 오류:', error);
    
    // 401 오류 처리 (인증 실패)
    if (error.response && error.response.status === 401) {
      // 로그인 페이지로 리다이렉트 (register나 login 페이지가 아닌 경우에만)
      const currentPath = router.currentRoute.value.path;
      if (currentPath !== '/login' && currentPath !== '/register') {
        localStorage.removeItem('token');
        router.push('/login');
      }
    }
    
    return Promise.reject(error);
  }
);

export default api; 