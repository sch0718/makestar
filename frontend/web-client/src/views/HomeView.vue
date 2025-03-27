<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const router = useRouter();
const authStore = useAuthStore();

// 로그인 상태에 따라 적절한 페이지로 이동
const navigateBasedOnAuth = () => {
  if (authStore.isLoggedIn) {
    router.push('/chat');
  } else {
    router.push('/login');
  }
};
</script>

<template>
  <div class="home-view">
    <div class="welcome-container">
      <h1 class="welcome-title">MakeStar Chat에 오신 것을 환영합니다</h1>
      <p class="welcome-subtitle">실시간으로 소통하는 새로운 경험</p>
      
      <div class="action-buttons">
        <button v-if="!authStore.isLoggedIn" @click="router.push('/login')" class="login-btn">
          로그인
        </button>
        <button v-if="!authStore.isLoggedIn" @click="router.push('/register')" class="register-btn">
          회원가입
        </button>
        <button v-if="authStore.isLoggedIn" @click="router.push('/chat')" class="chat-btn">
          채팅 시작하기
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.welcome-container {
  text-align: center;
  padding: 2rem;
  max-width: 600px;
}

.welcome-title {
  font-size: 2rem;
  color: #4a56e2;
  margin-bottom: 1rem;
}

.welcome-subtitle {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 2rem;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.login-btn, .register-btn, .chat-btn {
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.login-btn {
  background-color: #4a56e2;
  color: white;
}

.login-btn:hover {
  background-color: #3a46c2;
}

.register-btn {
  background-color: #fff;
  color: #4a56e2;
  border: 1px solid #4a56e2;
}

.register-btn:hover {
  background-color: #f0f2ff;
}

.chat-btn {
  background-color: #4a56e2;
  color: white;
}

.chat-btn:hover {
  background-color: #3a46c2;
}
</style> 