<script setup lang="ts">
import { computed } from 'vue';
import { useChatStore } from '@/store/chat';
import { useRouter } from 'vue-router';
import { ChatRoom } from '@/services/chatService';

const chatStore = useChatStore();
const router = useRouter();

const rooms = computed(() => chatStore.sortedRooms);
const selectedRoomId = computed(() => chatStore.currentRoom?.id);

const selectRoom = async (roomId: number) => {
  await chatStore.selectRoom(roomId);
};

const formatLastMessage = (room: ChatRoom) => {
  if (!room.lastMessage) return '메시지 없음';
  
  const content = room.lastMessage.content;
  return content.length > 30 ? content.substring(0, 30) + '...' : content;
};

const formatLastMessageTime = (room: ChatRoom) => {
  if (!room.lastMessage) return '';
  
  const date = new Date(room.lastMessage.createdAt);
  const now = new Date();
  
  // 오늘인 경우 시간만 표시 (HH:MM)
  if (date.toDateString() === now.toDateString()) {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }
  
  // 올해인 경우 월/일 표시
  if (date.getFullYear() === now.getFullYear()) {
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${month}/${day}`;
  }
  
  // 작년 이전인 경우 연도 포함 표시
  return `${date.getFullYear()}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getDate().toString().padStart(2, '0')}`;
};
</script>

<template>
  <div class="chat-room-list">
    <div class="chat-room-header">
      <h3>채팅</h3>
    </div>
    
    <div v-if="rooms.length === 0" class="empty-state">
      <p>채팅방이 없습니다.</p>
      <button class="btn btn-primary" @click="$emit('new-chat')">
        새 채팅 시작하기
      </button>
    </div>
    
    <div v-else class="room-list">
      <div
        v-for="room in rooms"
        :key="room.id"
        class="room-item"
        :class="{ 'selected': room.id === selectedRoomId }"
        @click="selectRoom(room.id)"
      >
        <div class="room-avatar">
          <!-- 프로필 이미지 또는 이니셜 -->
          {{ room.name.charAt(0).toUpperCase() }}
        </div>
        
        <div class="room-info">
          <div class="room-name">{{ room.name }}</div>
          <div class="room-last-message">{{ formatLastMessage(room) }}</div>
        </div>
        
        <div class="room-meta">
          <div class="room-time">{{ formatLastMessageTime(room) }}</div>
          <div v-if="room.unreadCount && room.unreadCount > 0" class="room-unread">
            {{ room.unreadCount }}
          </div>
        </div>
      </div>
    </div>
    
    <div class="new-chat-button">
      <button class="btn btn-primary rounded-circle" @click="$emit('new-chat')">
        +
      </button>
    </div>
  </div>
</template>

<style scoped>
.chat-room-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  border-right: 1px solid #eee;
}

.chat-room-header {
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.chat-room-header h3 {
  margin: 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 2rem;
  text-align: center;
  color: #999;
}

.room-list {
  flex: 1;
  overflow-y: auto;
}

.room-item {
  display: flex;
  padding: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
  border-bottom: 1px solid #f5f5f5;
}

.room-item:hover {
  background-color: #f8f9fa;
}

.room-item.selected {
  background-color: #e9ecef;
}

.room-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #4a56e2;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  font-weight: bold;
  margin-right: 1rem;
}

.room-info {
  flex: 1;
  min-width: 0;
}

.room-name {
  font-weight: 500;
  margin-bottom: 0.25rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.room-last-message {
  color: #6c757d;
  font-size: 0.875rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.room-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  min-width: 48px;
}

.room-time {
  font-size: 0.75rem;
  color: #6c757d;
  margin-bottom: 0.25rem;
}

.room-unread {
  background-color: #4a56e2;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
}

.new-chat-button {
  position: sticky;
  bottom: 1rem;
  right: 1rem;
  display: flex;
  justify-content: flex-end;
  padding: 1rem;
}

.rounded-circle {
  border-radius: 50%;
  width: 48px;
  height: 48px;
  font-size: 1.5rem;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}
</style> 