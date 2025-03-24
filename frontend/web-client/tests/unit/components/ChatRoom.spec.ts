import { mount } from '@vue/test-utils';
import { describe, it, expect, beforeEach, vi, MockInstance } from 'vitest';
import ChatRoom from '@/components/feature/ChatRoom.vue';
import { useChatStore } from '@/store/chat';
import { useAuthStore } from '@/store/auth';
import { useRouter } from 'vue-router';
import { chatService } from '@/services/chatService';
import { createPinia, setActivePinia } from 'pinia';

// 필요한 디펜던시 모킹
vi.mock('@/store/chat', () => ({
  useChatStore: vi.fn()
}));

vi.mock('@/store/auth', () => ({
  useAuthStore: vi.fn()
}));

vi.mock('vue-router', () => ({
  useRouter: vi.fn()
}));

vi.mock('@/services/chatService', () => ({
  chatService: {
    leaveRoom: vi.fn()
  }
}));

describe('ChatRoom.vue', () => {
  let chatStoreMock: any;
  let authStoreMock: any;
  let routerMock: any;
  
  beforeEach(() => {
    // Pinia 스토어 초기화
    setActivePinia(createPinia());
    
    // 목 객체 재설정
    vi.clearAllMocks();
    
    // 스토어 모킹
    chatStoreMock = {
      currentRoom: { id: 1, name: '테스트 채팅방' },
      messages: [
        { id: 1, content: '안녕하세요', username: 'user1', createdAt: new Date().toISOString() }
      ],
      typingUsers: { 1: [] },
      sendMessage: vi.fn(),
      sendTypingStatus: vi.fn(),
      loadChatRooms: vi.fn()
    };
    
    authStoreMock = {
      user: { id: 1 }
    };
    
    routerMock = {
      push: vi.fn()
    };
    
    // 모킹된 값 설정
    vi.mocked(useChatStore).mockReturnValue(chatStoreMock);
    vi.mocked(useAuthStore).mockReturnValue(authStoreMock);
    vi.mocked(useRouter).mockReturnValue(routerMock);
  });
  
  it('renders chat room name correctly', () => {
    const wrapper = mount(ChatRoom);
    expect(wrapper.find('.chat-header h3').text()).toBe('테스트 채팅방');
  });
  
  it('displays messages correctly', () => {
    const wrapper = mount(ChatRoom);
    expect(wrapper.text()).toContain('안녕하세요');
    expect(wrapper.text()).toContain('user1');
  });
  
  it('sends message on form submit', async () => {
    const wrapper = mount(ChatRoom);
    
    // 메시지 입력
    const input = wrapper.find('input[type="text"]');
    await input.setValue('테스트 메시지');
    
    // 폼 제출
    await wrapper.find('form').trigger('submit');
    
    // sendMessage 메서드가 호출되었는지 확인
    expect(chatStoreMock.sendMessage).toHaveBeenCalledWith('테스트 메시지');
    
    // 입력 필드가 초기화되었는지 확인
    expect((input.element as HTMLInputElement).value).toBe('');
  });
  
  it('handles typing status correctly', async () => {
    const wrapper = mount(ChatRoom);
    
    // 타이핑 시작
    await wrapper.find('input[type="text"]').trigger('input');
    
    // sendTypingStatus 메서드가 호출되었는지 확인
    expect(chatStoreMock.sendTypingStatus).toHaveBeenCalledWith(true);
    
    // 타이머 모킹이 필요하지만 생략
  });
  
  it('shows leave room button', () => {
    const wrapper = mount(ChatRoom);
    expect(wrapper.find('.leave-room-btn').exists()).toBe(true);
  });
  
  it('leaves chat room when leave button is clicked', async () => {
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(true);
    
    const wrapper = mount(ChatRoom);
    await wrapper.find('.leave-room-btn').trigger('click');
    
    // confirm이 호출되었는지 확인
    expect(confirmSpy).toHaveBeenCalled();
    
    // chatService.leaveRoom이 호출되었는지 확인
    expect(chatService.leaveRoom).toHaveBeenCalledWith(1);
    
    // loadChatRooms가 호출되었는지 확인
    expect(chatStoreMock.loadChatRooms).toHaveBeenCalled();
    
    confirmSpy.mockRestore();
  });
  
  it('does not leave room when user cancels', async () => {
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(false);
    
    const wrapper = mount(ChatRoom);
    await wrapper.find('.leave-room-btn').trigger('click');
    
    // confirm이 호출되었는지 확인
    expect(confirmSpy).toHaveBeenCalled();
    
    // chatService.leaveRoom이 호출되지 않았는지 확인
    expect(chatService.leaveRoom).not.toHaveBeenCalled();
    
    confirmSpy.mockRestore();
  });
  
  it('shows empty state when no room is selected', () => {
    // currentRoom을 null로 설정
    chatStoreMock.currentRoom = null;
    
    const wrapper = mount(ChatRoom);
    expect(wrapper.find('.no-room-selected').exists()).toBe(true);
    expect(wrapper.text()).toContain('채팅방을 선택하거나 새로운 채팅을 시작하세요');
  });
  
  it('shows empty message state when room has no messages', () => {
    // 메시지 목록을 비움
    chatStoreMock.messages = [];
    
    const wrapper = mount(ChatRoom);
    expect(wrapper.find('.empty-messages').exists()).toBe(true);
    expect(wrapper.text()).toContain('아직 메시지가 없습니다');
  });
}); 