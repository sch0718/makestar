<script setup lang="ts">
import { ref, computed } from 'vue';
import { useAuthStore } from '@/store/auth';
import { useRoute, useRouter } from 'vue-router';

const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();

const username = ref('');
const password = ref('');
const isSubmitting = computed(() => authStore.loading);
const error = computed(() => authStore.error);

const submitForm = async () => {
  if (!username.value || !password.value) return;
  
  const success = await authStore.login({
    username: username.value,
    password: password.value
  });
  
  if (success) {
    // 리디렉션 처리
    const redirectPath = route.query.redirect as string || '/chat';
    router.push(redirectPath);
  }
};
</script>

<template>
  <div class="login-page">
    <div class="card login-card">
      <div class="card-header">
        <h2 class="text-center">로그인</h2>
      </div>
      <div class="card-body">
        <form @submit.prevent="submitForm">
          <div v-if="error" class="error-message text-danger mb-3">
            {{ error }}
          </div>
          
          <div class="form-group">
            <label for="username">사용자 이름</label>
            <input
              type="text"
              id="username"
              v-model="username"
              class="form-control"
              placeholder="사용자 이름 입력"
              required
              :disabled="isSubmitting"
            />
          </div>
          
          <div class="form-group">
            <label for="password">비밀번호</label>
            <input
              type="password"
              id="password"
              v-model="password"
              class="form-control"
              placeholder="비밀번호 입력"
              required
              :disabled="isSubmitting"
            />
          </div>
          
          <div class="form-actions">
            <button
              type="submit"
              class="btn btn-primary w-100"
              :disabled="isSubmitting"
            >
              {{ isSubmitting ? '로그인 중...' : '로그인' }}
            </button>
          </div>
          
          <div class="text-center mt-3">
            <p>계정이 없으신가요? <router-link to="/register">회원가입</router-link></p>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.login-card {
  width: 100%;
  max-width: 400px;
}

.form-actions {
  margin-top: 1.5rem;
}

.w-100 {
  width: 100%;
}

.error-message {
  padding: 0.5rem;
  border-radius: 4px;
  background-color: rgba(220, 53, 69, 0.1);
}
</style> 