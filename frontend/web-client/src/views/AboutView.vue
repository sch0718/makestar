<template>
  <div class="about">
    <h1>MakeStar Chat 소개</h1>
    <div class="about-content">
      <p>
        MakeStar Chat은 실시간 채팅 애플리케이션으로, 사용자들이 쉽고 빠르게 소통할 수 있는 플랫폼입니다.
      </p>
      
      <h2>주요 기능</h2>
      <ul>
        <li>실시간 메시지 전송</li>
        <li>개인 및 그룹 채팅</li>
        <li>이미지 및 파일 공유</li>
        <li>사용자 상태 표시</li>
        <li>메시지 기록 보관</li>
      </ul>
      
      <h2>개발 환경</h2>
      <ul>
        <li>Frontend: Vue 3, TypeScript, Pinia</li>
        <li>Backend: Spring Boot, Spring Cloud</li>
        <li>데이터베이스: PostgreSQL, MongoDB, Redis</li>
        <li>메시징: RabbitMQ</li>
      </ul>
      
      <p>
        MakeStar Chat은 모던 웹 기술과 마이크로서비스 아키텍처를 기반으로 구축되었으며,
        안정적이고 확장 가능한 채팅 서비스를 제공합니다.
      </p>
    </div>
  </div>
</template>

<style scoped>
.about {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

h1 {
  color: #4a56e2;
  margin-bottom: 2rem;
  text-align: center;
}

h2 {
  color: #4a56e2;
  margin-top: 2rem;
  margin-bottom: 1rem;
}

.about-content {
  background-color: #f8f9fa;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

ul {
  margin-bottom: 1.5rem;
}

li {
  margin-bottom: 0.5rem;
}

p {
  line-height: 1.6;
  margin-bottom: 1.5rem;
}
</style> 