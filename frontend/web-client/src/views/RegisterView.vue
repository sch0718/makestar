<script setup lang="ts">
import { ref, computed } from 'vue';
import { useAuthStore } from '@/store/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const validationError = ref('');
const isSubmitting = computed(() => authStore.loading);
const error = computed(() => authStore.error);

const validateForm = () => {
  if (!username.value || !email.value || !password.value || !confirmPassword.value) {
    validationError.value = '모든 필드를 입력해주세요.';
    return false;
  }
  
  if (password.value !== confirmPassword.value) {
    validationError.value = '비밀번호가 일치하지 않습니다.';
    return false;
  }
  
  if (password.value.length < 6) {
    validationError.value = '비밀번호는 최소 6자 이상이어야 합니다.';
    return false;
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email.value)) {
    validationError.value = '유효한 이메일 주소를 입력해주세요.';
    return false;
  }
  
  validationError.value = '';
  return true;
};

const submitForm = async () => {
  if (!validateForm()) return;
  
  const success = await authStore.register({
    username: username.value,
    email: email.value,
    password: password.value
  });
  
  if (success) {
    router.push('/chat');
  }
};
</script>

<template>
  <div class="register-page">
    <div class="card register-card">
      <div class="card-header">
        <h2 class="text-center">회원가입</h2>
      </div>
      <div class="card-body">
        <form @submit.prevent="submitForm">
          <div v-if="validationError || error" class="error-message text-danger mb-3">
            {{ validationError || error }}
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
            <label for="email">이메일</label>
            <input
              type="email"
              id="email"
              v-model="email"
              class="form-control"
              placeholder="이메일 입력"
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
          
          <div class="form-group">
            <label for="confirm-password">비밀번호 확인</label>
            <input
              type="password"
              id="confirm-password"
              v-model="confirmPassword"
              class="form-control"
              placeholder="비밀번호 다시 입력"
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
              {{ isSubmitting ? '가입 중...' : '회원가입' }}
            </button>
          </div>
          
          <div class="text-center mt-3">
            <p>이미 계정이 있으신가요? <router-link to="/login">로그인</router-link></p>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.register-card {
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