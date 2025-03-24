<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '@/store/auth';
import userService, { UpdateProfileRequest } from '@/services/userService';

const authStore = useAuthStore();
const user = computed(() => authStore.user);

// 프로필 상태 관리
const status = ref<'ONLINE' | 'OFFLINE' | 'AWAY'>(user.value?.status || 'ONLINE');
const profileImageUrl = ref(user.value?.profileImageUrl || '');
const isUpdating = ref(false);
const updateError = ref<string | null>(null);
const updateSuccess = ref(false);

// 가능한 상태 옵션
const statusOptions = [
  { value: 'ONLINE', label: '온라인' },
  { value: 'OFFLINE', label: '오프라인' },
  { value: 'AWAY', label: '자리비움' }
];

// 프로필 업데이트
const updateProfile = async () => {
  if (!user.value) return;
  
  isUpdating.value = true;
  updateError.value = null;
  updateSuccess.value = false;
  
  try {
    const updateData: UpdateProfileRequest = {
      status: status.value,
      profileImageUrl: profileImageUrl.value
    };
    
    const updatedUser = await userService.updateProfile(updateData);
    
    // 유저 정보 업데이트
    authStore.$patch({ user: updatedUser });
    updateSuccess.value = true;
    
    // 성공 메시지 3초 후 제거
    setTimeout(() => {
      updateSuccess.value = false;
    }, 3000);
  } catch (error: any) {
    updateError.value = error.response?.data?.message || '프로필 업데이트에 실패했습니다.';
  } finally {
    isUpdating.value = false;
  }
};

// 컴포넌트 마운트 시 현재 값으로 초기화
onMounted(() => {
  if (user.value) {
    status.value = user.value.status || 'ONLINE';
    profileImageUrl.value = user.value.profileImageUrl || '';
  }
});
</script>

<template>
  <div class="profile-container">
    <h1 class="profile-title">프로필 설정</h1>
    
    <div v-if="!user" class="profile-loading">
      <p>사용자 정보를 불러오는 중...</p>
    </div>
    
    <form v-else @submit.prevent="updateProfile" class="profile-form">
      <div class="form-group">
        <label>사용자 이름</label>
        <input type="text" :value="user.username" disabled class="form-control" />
        <small class="form-text">사용자 이름은 변경할 수 없습니다.</small>
      </div>
      
      <div class="form-group">
        <label>이메일</label>
        <input type="email" :value="user.email" disabled class="form-control" />
        <small class="form-text">이메일은 변경할 수 없습니다.</small>
      </div>
      
      <div class="form-group">
        <label>상태</label>
        <select v-model="status" class="form-control">
          <option v-for="option in statusOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
      </div>
      
      <div class="form-group">
        <label>프로필 이미지 URL</label>
        <input 
          type="url" 
          v-model="profileImageUrl" 
          placeholder="https://example.com/image.jpg" 
          class="form-control"
        />
      </div>
      
      <div class="profile-preview" v-if="profileImageUrl">
        <img :src="profileImageUrl" alt="프로필 이미지 미리보기" class="preview-image" />
      </div>
      
      <div v-if="updateError" class="error-message">
        {{ updateError }}
      </div>
      
      <div v-if="updateSuccess" class="success-message">
        프로필이 성공적으로 업데이트되었습니다!
      </div>
      
      <button type="submit" class="submit-button" :disabled="isUpdating">
        {{ isUpdating ? '업데이트 중...' : '프로필 업데이트' }}
      </button>
    </form>
  </div>
</template>

<style scoped>
.profile-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

.profile-title {
  margin-bottom: 2rem;
  text-align: center;
  color: #4a56e2;
}

.profile-loading {
  text-align: center;
  color: #6c757d;
}

.profile-form {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 1rem;
}

.form-control:disabled {
  background-color: #f8f9fa;
  cursor: not-allowed;
}

.form-text {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.875rem;
  color: #6c757d;
}

.profile-preview {
  margin: 1.5rem 0;
  text-align: center;
}

.preview-image {
  max-width: 150px;
  max-height: 150px;
  border-radius: 50%;
  border: 3px solid #4a56e2;
}

.error-message {
  padding: 0.75rem;
  margin-bottom: 1rem;
  background-color: #f8d7da;
  color: #721c24;
  border-radius: 4px;
}

.success-message {
  padding: 0.75rem;
  margin-bottom: 1rem;
  background-color: #d4edda;
  color: #155724;
  border-radius: 4px;
}

.submit-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #4a56e2;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-button:hover:not(:disabled) {
  background-color: #3a46c2;
}

.submit-button:disabled {
  background-color: #b1b6f0;
  cursor: not-allowed;
}
</style> 