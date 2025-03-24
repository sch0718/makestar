import { rest } from 'msw';

export const handlers = [
  // Auth 관련 핸들러
  rest.post('/api/auth/login', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json({
        id: 1,
        username: 'testUser',
        email: 'test@example.com',
        token: 'fake-jwt-token',
        status: 'ONLINE',
        profileImageUrl: 'https://example.com/profile.jpg',
      })
    );
  }),

  // 채팅방 관련 핸들러
  rest.get('/api/chat/rooms', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json([
        {
          id: 1,
          name: '테스트 채팅방',
          description: '테스트 설명',
          type: 'GROUP',
          lastMessage: {
            id: 1,
            content: '안녕하세요',
            username: 'user1',
            createdAt: new Date().toISOString(),
          },
          unreadCount: 0,
          createdAt: new Date().toISOString(),
        },
      ])
    );
  }),

  // 메시지 관련 핸들러
  rest.get('/api/chat/rooms/:roomId/messages', (req, res, ctx) => {
    const roomId = req.params.roomId;
    return res(
      ctx.status(200),
      ctx.json([
        {
          id: 1,
          roomId: Number(roomId),
          userId: 2,
          username: 'user1',
          content: '안녕하세요',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          isRead: true,
        },
      ])
    );
  }),

  rest.delete('/api/chat/rooms/:roomId/leave', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json({ success: true })
    );
  }),
]; 