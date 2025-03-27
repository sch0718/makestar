// TypeScript 타입 선언 추가
declare global {
  interface Window {
    global: Window;
    WebSocket: any;
  }
}

// 브라우저 환경에서 Node.js global 객체 폴리필
window.global = window;

import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
import axios from 'axios';

import './assets/main.css';

// Axios 기본 설정
axios.defaults.baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
axios.defaults.withCredentials = true; // 쿠키를 포함한 요청 허용

// 토큰이 있으면 헤더에 추가
const token = localStorage.getItem('token');
if (token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

// 요청 인터셉터 - CSRF 토큰 및 인증 토큰 처리
axios.interceptors.request.use(
  (config) => {
    // CSRF 토큰 처리
    const csrfToken = document.cookie
      .split('; ')
      .find((row) => row.startsWith('XSRF-TOKEN='))
      ?.split('=')[1];
    
    if (csrfToken) {
      config.headers['X-XSRF-TOKEN'] = csrfToken;
    }
    
    // 요청 시점에 토큰이 업데이트되었을 수 있으므로 다시 확인
    const currentToken = localStorage.getItem('token');
    if (currentToken) {
      config.headers.Authorization = `Bearer ${currentToken}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.mount('#app'); 