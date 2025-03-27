<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useChatStore } from '@/store/chat';
import { useAuthStore } from '@/store/auth';
import ChatRoomList from '@/components/feature/ChatRoomList.vue';
import ChatRoom from '@/components/feature/ChatRoom.vue';

const router = useRouter();
const chatStore = useChatStore();
const authStore = useAuthStore();
const showNewRoomModal = ref(false);

// 로그인 확인
onMounted(async () => {
  if (!authStore.isLoggedIn) {
    router.push('/login');
    return;
  }

  try {
    // 채팅방 목록 로드
    await chatStore.loadChatRooms();
    
    // 웹소켓 연결 초기화
    chatStore.initSocket();
  } catch (error) {
    console.error('채팅 초기화 오류:', error);
  }
});

// 컴포넌트 언마운트 시 연결 해제
onUnmounted(() => {
  chatStore.disconnectSocket();
});

// 새 채팅방 생성 페이지로 이동
const navigateToNewRoom = () => {
  router.push('/chat/new');
};
</script>

<template>
  <div class="chat-view">
    <div class="chat-container">
      <div class="sidebar">
        <div class="sidebar-header">
          <h2>채팅</h2>
          <button class="new-chat-btn" @click="navigateToNewRoom">
            <i class="fas fa-plus"></i> 새 채팅
          </button>
        </div>
        
        <ChatRoomList />
      </div>
      
      <div class="main-content">
        <ChatRoom />
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-view {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.chat-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: 300px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #eee;
  background-color: #f8f9fa;
}

.sidebar-header {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 1.5rem;
}

.new-chat-btn {
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.new-chat-btn:hover {
  background-color: #0069d9;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

@media (max-width: 768px) {
  .chat-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: 300px;
    border-right: none;
    border-bottom: 1px solid #eee;
  }
}
</style> 