// vitest 설정
import { expect, afterEach } from 'vitest';
import { cleanup } from '@testing-library/vue';
import '@testing-library/jest-dom';

// 각 테스트 후 Vue 컴포넌트 정리
afterEach(() => {
  cleanup();
}); 