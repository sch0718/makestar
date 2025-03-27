<script setup lang="ts">
import { RouterView } from 'vue-router';
import { useAuthStore } from '@/store/auth';
import { onMounted } from 'vue';

const authStore = useAuthStore();

onMounted(() => {
  // 저장된 토큰이 있으면 사용자 정보 로드
  if (authStore.isLoggedIn) {
    authStore.loadUser();
  }
});
</script>

<template>
  <header class="app-header">
    <div class="container">
      <h1 class="app-title">MakeStar Chat</h1>
      <nav v-if="authStore.isLoggedIn" class="nav-menu">
        <router-link to="/chat">채팅</router-link>
        <router-link to="/profile">프로필</router-link>
        <a href="#" @click.prevent="authStore.logout">로그아웃</a>
      </nav>
    </div>
  </header>

  <main class="app-main">
    <div class="container">
      <RouterView />
    </div>
  </main>

  <footer class="app-footer">
    <div class="container">
      <p>&copy; 2025 MakeStar Chat. All rights reserved.</p>
    </div>
  </footer>
</template>

<style scoped>
.app-header {
  background-color: #4a56e2;
  color: white;
  padding: 1rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.app-title {
  margin: 0;
  font-size: 1.5rem;
}

.nav-menu {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
}

.nav-menu a {
  color: white;
  text-decoration: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.nav-menu a:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.app-main {
  min-height: calc(100vh - 180px);
  padding: 2rem 0;
}

.app-footer {
  background-color: #f8f9fa;
  padding: 1rem 0;
  text-align: center;
  font-size: 0.875rem;
  color: #6c757d;
}
</style> 