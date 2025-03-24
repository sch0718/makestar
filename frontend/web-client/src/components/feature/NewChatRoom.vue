<script setup lang="ts">
import { ref } from 'vue';
import { useChatStore } from '@/store/chat';
import { useRouter } from 'vue-router';

const router = useRouter();
const chatStore = useChatStore();

const roomName = ref('');
const usernames = ref('');
const loading = ref(false);
const error = ref('');

const createRoom = async () => {
  if (!roomName.value.trim()) {
    error.value = '방 이름을 입력해주세요.';
    return;
  }

  if (!usernames.value.trim()) {
    error.value = '최소 한 명의 사용자를 추가해주세요.';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    // 쉼표로 구분된 사용자 이름을 배열로 변환
    const usernameArray = usernames.value
      .split(',')
      .map(username => username.trim())
      .filter(username => username);

    const newRoom = await chatStore.createRoom({
      name: roomName.value.trim(),
      participants: usernameArray
    });

    // 생성 후 새 방으로 이동
    if (newRoom) {
      roomName.value = '';
      usernames.value = '';
      router.push('/chat');
    }
  } catch (err) {
    if (err instanceof Error) {
      error.value = err.message;
    } else {
      error.value = '채팅방 생성 중 오류가 발생했습니다.';
    }
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="new-chat-room">
    <h2>새 채팅방 만들기</h2>
    
    <form @submit.prevent="createRoom" class="new-room-form">
      <div class="form-group">
        <label for="roomName">방 이름</label>
        <input
          type="text"
          id="roomName"
          v-model="roomName"
          placeholder="채팅방 이름을 입력하세요"
          class="form-control"
        />
      </div>
      
      <div class="form-group">
        <label for="usernames">참여자</label>
        <input
          type="text"
          id="usernames"
          v-model="usernames"
          placeholder="사용자 이름을 쉼표로 구분하여 입력하세요"
          class="form-control"
        />
        <small class="form-text text-muted">예: user1, user2, user3</small>
      </div>
      
      <div v-if="error" class="error-message">{{ error }}</div>
      
      <button type="submit" class="btn btn-primary" :disabled="loading">
        {{ loading ? '생성 중...' : '채팅방 만들기' }}
      </button>
    </form>
  </div>
</template>

<style scoped>
.new-chat-room {
  max-width: 500px;
  margin: 0 auto;
  padding: 2rem;
}

h2 {
  margin-bottom: 1.5rem;
  text-align: center;
}

.new-room-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-control {
  padding: 0.5rem;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
}

.error-message {
  color: #dc3545;
  margin-bottom: 1rem;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.25rem;
  cursor: pointer;
  font-weight: 500;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0069d9;
}

.btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.form-text {
  font-size: 0.875rem;
  color: #6c757d;
}
</style> 