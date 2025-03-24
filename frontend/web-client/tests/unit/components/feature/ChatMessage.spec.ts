import { mount } from '@vue/test-utils';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import ChatMessage from '@/components/feature/ChatMessage.vue';
import { useAuthStore } from '@/store/auth';
import { createPinia, setActivePinia } from 'pinia';

// useAuthStore를 모킹
vi.mock('@/store/auth', () => ({
  useAuthStore: vi.fn()
}));

describe('ChatMessage.vue', () => {
  beforeEach(() => {
    // Pinia 스토어 초기화
    setActivePinia(createPinia());
    
    // useAuthStore 목 구현
    vi.mocked(useAuthStore).mockReturnValue({
      user: { id: 1 },
      currentUser: { id: 1 }
    } as any);
  });

  it('renders message content correctly', () => {
    const message = {
      id: 1,
      roomId: 1,
      userId: 2,
      username: 'testUser',
      content: 'Hello, world!',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      isRead: true
    };

    const wrapper = mount(ChatMessage, {
      props: { message }
    });

    // 메시지 내용이 올바르게 표시되는지 확인
    expect(wrapper.text()).toContain('Hello, world!');
    expect(wrapper.text()).toContain('testUser');
  });

  it('formats timestamp correctly', () => {
    // 특정 시간으로 고정하여 테스트
    const fixedDate = new Date('2023-03-23T10:30:15');
    const message = {
      id: 1,
      roomId: 1,
      userId: 2,
      username: 'testUser',
      content: 'Time test',
      createdAt: fixedDate.toISOString(),
      updatedAt: fixedDate.toISOString(),
      isRead: true
    };

    const wrapper = mount(ChatMessage, {
      props: { message }
    });

    // HH:MM 형식으로 시간이 표시되는지 확인
    expect(wrapper.text()).toContain('10:30');
  });

  it('applies correct styling for own messages', () => {
    // 현재 사용자의 메시지
    const myMessage = {
      id: 1,
      roomId: 1,
      userId: 1, // 현재 사용자와 동일
      username: 'me',
      content: 'My message',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      isRead: true
    };

    const wrapper = mount(ChatMessage, {
      props: { message: myMessage }
    });

    // 자신의 메시지에는 own-message 클래스가 적용되어야 함
    expect(wrapper.find('.message-container').classes()).toContain('own-message');
  });

  it('applies correct styling for others\' messages', () => {
    // 다른 사용자의 메시지
    const otherMessage = {
      id: 1,
      roomId: 1,
      userId: 2, // 현재 사용자와 다름
      username: 'other',
      content: 'Other message',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      isRead: true
    };

    const wrapper = mount(ChatMessage, {
      props: { message: otherMessage }
    });

    // 다른 사용자의 메시지에는 other-message 클래스가 적용되어야 함
    expect(wrapper.find('.message-container').classes()).toContain('other-message');
    // 사용자 이름이 표시되어야 함
    expect(wrapper.text()).toContain('other');
  });
}); 