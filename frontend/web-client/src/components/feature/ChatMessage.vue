<script setup lang="ts">
import { computed } from 'vue';
import { useAuthStore } from '@/store/auth';

interface Message {
  id: number;
  roomId: number;
  userId: number;
  username: string;
  content: string;
  createdAt: string;
  updatedAt: string;
  isRead: boolean;
}

const props = defineProps<{
  message: Message;
}>();

const authStore = useAuthStore();
const currentUserId = computed(() => authStore.user?.id);
const isOwnMessage = computed(() => props.message.userId === currentUserId.value);

// HH:MM 형식으로 시간 포맷팅
const formattedTime = computed(() => {
  const date = new Date(props.message.createdAt);
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
});
</script>

<template>
  <div class="message-container" :class="{ 'own-message': isOwnMessage, 'other-message': !isOwnMessage }">
    <div v-if="!isOwnMessage" class="message-sender">{{ message.username }}</div>
    <div class="message-content">
      <p>{{ message.content }}</p>
      <span class="message-time">{{ formattedTime }}</span>
    </div>
  </div>
</template>

<style scoped>
.message-container {
  display: flex;
  flex-direction: column;
  margin-bottom: 1rem;
  max-width: 70%;
}

.own-message {
  align-self: flex-end;
  align-items: flex-end;
}

.other-message {
  align-self: flex-start;
  align-items: flex-start;
}

.message-sender {
  font-size: 0.85rem;
  color: #6c757d;
  margin-bottom: 0.25rem;
}

.message-content {
  position: relative;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  word-break: break-word;
}

.own-message .message-content {
  background-color: #4a56e2;
  color: white;
  border-bottom-right-radius: 0;
}

.other-message .message-content {
  background-color: #f1f3f5;
  color: #495057;
  border-bottom-left-radius: 0;
}

.message-time {
  font-size: 0.75rem;
  margin-top: 0.25rem;
  position: absolute;
  bottom: -1.25rem;
  opacity: 0.7;
}

.own-message .message-time {
  right: 0.25rem;
  color: #6c757d;
}

.other-message .message-time {
  left: 0.25rem;
  color: #6c757d;
}
</style> 