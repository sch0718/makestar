<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue';
import { useChatStore } from '@/store/chat';
import { useAuthStore } from '@/store/auth';
import { useRouter } from 'vue-router';
import { chatService } from '@/services/chatService';
import ChatMessage from './ChatMessage.vue';

const chatStore = useChatStore();
const authStore = useAuthStore();
const router = useRouter();

const messageInput = ref('');
const messageContainer = ref<HTMLElement | null>(null);
const isTyping = ref(false);
const typingTimeout = ref<number | null>(null);
const isLeavingRoom = ref(false);

const currentRoom = computed(() => chatStore.currentRoom);
const messages = computed(() => chatStore.messages);
const typingUsers = computed(() => {
  if (!currentRoom.value) return [];
  
  const roomId = currentRoom.value.id;
  return chatStore.typingUsers[roomId] || [];
});

// 타이핑 상태 전송
const handleInput = () => {
  if (!isTyping.value) {
    isTyping.value = true;
    chatStore.sendTypingStatus(true);
  }
  
  // 타이핑 중단 3초 후에 상태 업데이트
  if (typingTimeout.value) {
    clearTimeout(typingTimeout.value);
  }
  
  typingTimeout.value = window.setTimeout(() => {
    isTyping.value = false;
    chatStore.sendTypingStatus(false);
  }, 3000);
};

// 메시지 전송
const sendMessage = () => {
  if (!messageInput.value.trim()) return;
  
  chatStore.sendMessage(messageInput.value.trim());
  messageInput.value = '';
  
  // 타이핑 상태 중지
  isTyping.value = false;
  chatStore.sendTypingStatus(false);
  
  if (typingTimeout.value) {
    clearTimeout(typingTimeout.value);
  }
};

// 채팅방 나가기
const leaveRoom = async () => {
  if (!currentRoom.value || isLeavingRoom.value) return;
  
  const confirmed = window.confirm(`정말로 "${currentRoom.value.name}" 채팅방을 나가시겠습니까?`);
  if (!confirmed) return;
  
  isLeavingRoom.value = true;
  
  try {
    await chatService.leaveRoom(currentRoom.value.id);
    
    // 채팅방 목록에서 제거
    await chatStore.loadChatRooms();
    
    // 채팅방 선택 초기화
    chatStore.currentRoomId = null;
    
    // 메시지 초기화
    chatStore.messages = [];
  } catch (error) {
    console.error('채팅방 나가기 실패:', error);
    alert('채팅방 나가기에 실패했습니다. 다시 시도해주세요.');
  } finally {
    isLeavingRoom.value = false;
  }
};

// 스크롤을 항상 최신 메시지로 유지
const scrollToBottom = async () => {
  await nextTick();
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
  }
};

// 메시지 변화 감지해서 스크롤 조정
watch(messages, scrollToBottom, { deep: true });

// 타이핑 상태 정리
onUnmounted(() => {
  if (typingTimeout.value) {
    clearTimeout(typingTimeout.value);
  }
  
  if (isTyping.value) {
    chatStore.sendTypingStatus(false);
  }
});

// 컴포넌트 마운트 시 스크롤 초기화
onMounted(scrollToBottom);
</script>

<template>
  <div class="chat-room">
    <div v-if="!currentRoom" class="no-room-selected">
      <p>채팅방을 선택하거나 새로운 채팅을 시작하세요.</p>
    </div>
    
    <template v-else>
      <div class="chat-header">
        <h3>{{ currentRoom.name }}</h3>
        <button 
          @click="leaveRoom" 
          class="leave-room-btn" 
          :disabled="isLeavingRoom"
          title="채팅방 나가기"
        >
          <span v-if="isLeavingRoom">나가는 중...</span>
          <span v-else>나가기</span>
        </button>
      </div>
      
      <div class="chat-messages" ref="messageContainer">
        <div v-if="messages.length === 0" class="empty-messages">
          <p>아직 메시지가 없습니다. 첫 메시지를 보내보세요!</p>
        </div>
        
        <template v-else>
          <ChatMessage
            v-for="message in messages"
            :key="message.id"
            :message="message"
          />
        </template>
        
        <div v-if="typingUsers.length > 0" class="typing-indicator">
          <span>{{ typingUsers.join(', ') }} 님이 입력 중...</span>
        </div>
      </div>
      
      <div class="chat-input">
        <form @submit.prevent="sendMessage">
          <div class="input-group">
            <input
              type="text"
              v-model="messageInput"
              @input="handleInput"
              placeholder="메시지를 입력하세요..."
              class="form-control"
            />
            <button type="submit" class="btn btn-primary">전송</button>
          </div>
        </form>
      </div>
    </template>
  </div>
</template>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.no-room-selected {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #6c757d;
  text-align: center;
  padding: 2rem;
}

.chat-header {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h3 {
  margin: 0;
}

.leave-room-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.leave-room-btn:hover:not(:disabled) {
  background-color: #c82333;
}

.leave-room-btn:disabled {
  background-color: #e9a8ae;
  cursor: not-allowed;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
}

.empty-messages {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #6c757d;
  text-align: center;
}

.typing-indicator {
  padding: 0.5rem;
  color: #6c757d;
  font-style: italic;
  font-size: 0.875rem;
}

.chat-input {
  padding: 1rem;
  border-top: 1px solid #eee;
}

.input-group {
  display: flex;
}

.input-group .form-control {
  flex: 1;
  border: 1px solid #ced4da;
  border-radius: 4px;
  padding: 0.5rem;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.input-group .btn {
  background-color: #4a56e2;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  cursor: pointer;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

.input-group .btn:hover {
  background-color: #3a46c2;
}
</style> 