import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
import axios from 'axios';

import './assets/main.css';

// CSRF 토큰 처리를 위한 axios 설정
axios.defaults.withCredentials = true; // 쿠키 전송 활성화

// 요청 인터셉터 추가
axios.interceptors.request.use(function (config) {
  // 쿠키에서 XSRF-TOKEN 가져오기
  const token = getCookie('XSRF-TOKEN');
  if (token) {
    // Spring Boot의 기본 헤더 이름
    config.headers['X-XSRF-TOKEN'] = token;
  }
  return config;
}, function (error) {
  return Promise.reject(error);
});

// 쿠키 값 가져오는 함수
function getCookie(name: string): string | null {
  const cookies = document.cookie.split('; ');
  for (const cookie of cookies) {
    const [cookieName, cookieValue] = cookie.split('=');
    if (cookieName === name) {
      return decodeURIComponent(cookieValue);
    }
  }
  return null;
}

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.mount('#app'); 