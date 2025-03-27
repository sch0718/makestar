/// <reference types="vite/client" />

declare module '@stomp/stompjs' {
  export * from '@stomp/stompjs';
}

declare module 'stompjs' {
  export * from 'stompjs';
}

declare module 'sockjs-client' {
  const SockJS: any;
  export default SockJS;
} 