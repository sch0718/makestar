import { setupServer } from 'msw/node';
import { handlers } from './handlers';
import { beforeAll, afterEach, afterAll } from 'vitest';

// MSW 서버 설정
export const server = setupServer(...handlers);

// 서버 리스너 시작 및 테스트 오류 캡처
beforeAll(() => server.listen({ onUnhandledRequest: 'error' }));

// 각 테스트 간에 핸들러 재설정
afterEach(() => server.resetHandlers());

// 테스트 종료 후 서버 종료
afterAll(() => server.close()); 