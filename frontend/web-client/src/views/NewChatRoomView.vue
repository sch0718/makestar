<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';
import NewChatRoom from '@/components/feature/NewChatRoom.vue';

const router = useRouter();
const authStore = useAuthStore();

// 로그인 확인
onMounted(() => {
  if (!authStore.isLoggedIn) {
    router.push('/login');
  }
});

// 뒤로 가기
const goBack = () => {
  router.push('/chat');
};
</script>

<template>
  <div class="new-chat-room-view">
    <div class="new-chat-room-container">
      <div class="header">
        <button class="back-btn" @click="goBack">
          <i class="fas fa-arrow-left"></i> 뒤로
        </button>
      </div>
      
      <NewChatRoom />
    </div>
  </div>
</template>

<style scoped>
.new-chat-room-view {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f8f9fa;
}

.new-chat-room-container {
  width: 100%;
  max-width: 600px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.header {
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.back-btn {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0;
  font-size: 0.875rem;
}

.back-btn:hover {
  text-decoration: underline;
}
</style> 