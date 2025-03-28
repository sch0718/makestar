# 메이크스타 코딩 테스트

## 프로젝트 소개

> 여기에 작성된 코드들은 다음과 같은 환경에서 작성되었음을 알려드립니다.  
> - IDE: Curosr
> - AI의 도움을 받아 코드를 작성했습니다.
> - claude-3.7-sonnet 모델을 사용하였습니다.
> - Agent와 MCP를 최대한 활용하였습니다.

이 프로젝트는 채팅을 위한 서버와 클라이언트 두 가지 주요 기능을 포함하고 있습니다.

개발 과정과 이 과제의 내용입니다.

- [문제 보기](./docs/TEST.md)

- [삽질 보기](./docs/TROUBLE_SHOOTING.md)

## 시스템 개요

MakeStar는 마이크로서비스 아키텍처(MSA)를 기반으로 구축된 실시간 채팅 애플리케이션입니다. 사용자들이 개인 또는 그룹 채팅방을 통해 실시간으로 메시지를 주고받을 수 있는 기능을 제공합니다. 확장성과 유지보수성을 고려하여 각 기능별로 독립적인 서비스로 분리하여 구성하였습니다.

### 주요 기능

- 사용자 등록 및 인증
- 실시간 채팅 (개인 및 그룹)
- 메시지 히스토리 저장 및 조회
- 사용자 상태 관리
- 채팅방 관리 (생성, 참여, 나가기)

## 시스템 아키텍처

MakeStar 채팅 시스템은 모노레포(Monorepo) 구조로 다음과 같은 마이크로서비스로 구성되어 있습니다:

```mermaid
graph TD
    Client[클라이언트] --> Gateway[API Gateway]
    Gateway --> AuthService[인증 서비스]
    Gateway --> UserService[사용자 서비스]
    Gateway --> ChatService[채팅 서비스]
    Gateway --> HistoryService[히스토리 서비스]
    
    AuthService --> UserDB[(사용자 DB)]
    UserService --> UserDB
    ChatService --> ChatDB[(채팅 DB)]
    HistoryService --> HistoryDB[(히스토리 DB)]
    
    ChatService <-.-> MessageBroker{메시지 브로커}
    MessageBroker <-.-> HistoryService
    
    DiscoveryService[Discovery Service] <-.-> Gateway
    DiscoveryService <-.-> AuthService
    DiscoveryService <-.-> UserService
    DiscoveryService <-.-> ChatService
    DiscoveryService <-.-> HistoryService
    
    subgraph "Frontend"
        Client
    end
    
    subgraph "Backend Services"
        Gateway
        AuthService
        UserService
        ChatService
        HistoryService
        MessageBroker
        DiscoveryService
    end
    
    subgraph "Data Layer"
        UserDB
        ChatDB
        HistoryDB
    end
```

### 기술 스택

- **백엔드**:
  - Java 11+
  - Spring Boot 2.7.x
  - Spring Cloud 2021.0.x (Gateway, OpenFeign, Eureka, Circuit Breaker)
  - Spring Security + JWT
  - Spring WebSocket
  - JPA / Hibernate
  - SpringDoc OpenAPI (API 문서화)
  - PostgreSQL (Supabase)
  - MongoDB Atlas

- **프론트엔드**:
  - Vue.js 3
  - TypeScript
  - Vite
  - Pinia (상태 관리)
  - WebSocket

- **인프라**:
  - Docker & Docker Compose
  - Gradle (빌드 도구)
  - Git (버전 관리)

## 모노레포 구조

프로젝트는 다음과 같은 모노레포 구조로 관리됩니다:

```
makestar/
├── services/                       # 백엔드 마이크로서비스
│   ├── discovery-service/          # Discovery 서비스 (Eureka)
│   ├── api-gateway/                # API Gateway 서비스
│   ├── auth-service/               # 인증 서비스
│   ├── user-service/               # 사용자 서비스
│   ├── chat-service/               # 채팅 서비스
│   └── history-service/            # 히스토리 서비스
├── frontend/                       # 프론트엔드 애플리케이션
│   └── web-client/                 # Vue 웹 클라이언트
├── shared/                         # 공유 라이브러리 및 모듈
│   ├── commons/                    # 공통 클래스
├── docs/                           # 프로젝트 문서
├── settings.gradle                 # Gradle 설정
├── build.gradle                    # 루트 빌드 파일
└── README.md                       # 프로젝트 소개
```

## 서비스 설명

### Discovery Service

Discovery Service는 Netflix Eureka를 기반으로 서비스 등록 및 검색을 담당합니다:

- 모든 마이크로서비스의 등록 처리
- 서비스 위치 정보 제공
- 상태 모니터링 및 장애 감지
- 로드 밸런싱 지원

### API Gateway

API Gateway는 Spring Cloud Gateway를 기반으로 구현되어 모든 클라이언트 요청의 진입점 역할을 합니다:

- 요청 라우팅: 클라이언트 요청을 적절한 마이크로서비스로 라우팅
- 인증 및 권한 부여: JWT 토큰 검증을 통한 사용자 인증
- 보안 설정: CORS, CSRF 보호 기능 제공
- 요청/응답 로깅: 시스템 모니터링을 위한 로깅
- 부하 분산: 서비스 인스턴스 간 부하 분산

### 인증 서비스 (Auth Service)

인증 서비스는 사용자 인증 및 권한 관리를 담당합니다:

- 사용자 등록 (회원가입)
- 로그인 및 JWT 토큰 발급
- 토큰 갱신 및 검증
- 비밀번호 재설정

### 사용자 서비스 (User Service)

사용자 서비스는 사용자 정보 및 프로필 관리를 담당합니다:

- 사용자 프로필 관리
- 사용자 검색
- 친구 목록 관리
- 사용자 상태 관리 (온라인/오프라인)

### 채팅 서비스 (Chat Service)

채팅 서비스는 WebSocket을 기반으로 실시간 메시징 기능을 제공합니다:

- WebSocket 기반 실시간 메시지 교환
- 채팅방 생성 및 관리
- 메시지 브로드캐스팅
- 읽음 확인 기능
- MongoDB Atlas를 통한 채팅 데이터 저장

### 히스토리 서비스 (History Service)

히스토리 서비스는 채팅 메시지의 저장 및 조회를 담당합니다:

- 메시지 영구 저장
- 채팅 기록 조회
- 메시지 검색
- 채팅 통계 제공

## API 문서화

모든 백엔드 서비스는 SpringDoc OpenAPI(Swagger)를 사용하여 API 문서를 자동 생성합니다:

- 각 서비스별 API 엔드포인트, 요청/응답 스키마 자동 문서화
- JWT 인증 설정 포함
- API 테스트 인터페이스 제공

문서 접근 경로:
- http://localhost:8080/api-docs - 통합 API 문서 (Gateway)
- http://localhost:8081/api-docs - 인증 서비스 API 문서
- http://localhost:8082/api-docs - 사용자 서비스 API 문서
- http://localhost:8083/api-docs - 채팅 서비스 API 문서
- http://localhost:8084/api-docs - 히스토리 서비스 API 문서

## 보안 설정

시스템은 다양한 보안 기능을 통합하고 있습니다:

- API Gateway 수준에서의 인증 및 권한 관리
  - JWT 기반 인증 및 권한 부여
  - CORS 설정을 통한 허용된 출처 관리
  - CSRF 보호 설정
  - 경로 기반 보안 규칙 적용
- 마이크로서비스 간 통신 보안
  - 내부 서비스 통신은 API Gateway를 통해서만 가능
  - 인증된 요청에 대해서만 서비스 간 통신 허용
- 비밀번호 암호화 저장
- HTTPS 통신 지원
- 입력 유효성 검사

## 데이터베이스

MakeStar 시스템은 다중 데이터베이스 아키텍처를 채택하고 있습니다:

- **Supabase (PostgreSQL)**
  - 사용자 데이터 관리 (인증 서비스, 사용자 서비스)
  - 채팅방 메타데이터 관리
  - 히스토리 데이터 저장

- **MongoDB Atlas**
  - 실시간 채팅 메시지 저장
  - 비정형 데이터 처리
  - 빠른 쿼리 성능

[데이터베이스 아키텍처 보기](./docs/DB_ARCHITECTURE.md)

## 워크플로우

### 사용자 등록 및 로그인 워크플로우

```mermaid
sequenceDiagram
    Client->>API Gateway: 회원가입 요청
    API Gateway->>Auth Service: 회원가입 요청 전달
    Auth Service->>Auth Service: 사용자 정보 검증
    Auth Service->>User Service: 사용자 정보 저장 요청
    User Service->>Auth Service: 저장 완료 응답
    Auth Service->>API Gateway: 회원가입 완료 응답
    API Gateway->>Client: 회원가입 완료 응답
    
    Client->>API Gateway: 로그인 요청
    API Gateway->>Auth Service: 로그인 요청 전달
    Auth Service->>Auth Service: 사용자 인증
    Auth Service->>API Gateway: JWT 토큰 발급
    API Gateway->>Client: JWT 토큰 반환
```

### 실시간 채팅 워크플로우

```mermaid
sequenceDiagram
    Client->>API Gateway: WebSocket 연결 요청 (+ JWT)
    API Gateway->>Auth Service: 토큰 검증 요청
    Auth Service->>API Gateway: 토큰 유효성 응답
    API Gateway->>Chat Service: WebSocket 연결 전달
    Chat Service->>Client: WebSocket 연결 설정
    
    Client->>Chat Service: 메시지 전송
    Chat Service->>Message Broker: 메시지 발행
    Message Broker->>Chat Service: 메시지 브로드캐스트
    Chat Service->>Other Clients: 메시지 전달
    Message Broker->>History Service: 메시지 저장 요청
    History Service->>History DB: 메시지 저장
```

### 채팅 기록 조회 워크플로우

```mermaid
sequenceDiagram
    Client->>API Gateway: 채팅 기록 요청 (+ JWT)
    API Gateway->>Auth Service: 토큰 검증 요청
    Auth Service->>API Gateway: 토큰 유효성 응답
    API Gateway->>History Service: 채팅 기록 요청 전달
    History Service->>History DB: 기록 조회
    History DB->>History Service: 조회 결과 반환
    History Service->>API Gateway: 채팅 기록 응답
    API Gateway->>Client: 채팅 기록 전달
```

## 실행 방법

### 사전 요구사항

- JDK 11 이상
- Gradle 7.0 이상
- Docker 및 Docker Compose (선택 사항)
- Node.js 16 이상 (프론트엔드 개발 시)

### 백엔드 서비스 실행

#### 방법 1: 개별 서비스 실행

```bash
# 프로젝트 클론
git clone https://github.com/yourusername/makestar.git
cd makestar

# 빌드
./gradlew clean build

# 서비스 순차적 실행 (별도의 터미널에서)
java -jar services/discovery-service/build/libs/discovery-service-0.0.1-SNAPSHOT.jar  # 먼저 실행
java -jar services/api-gateway/build/libs/api-gateway-0.0.1-SNAPSHOT.jar
java -jar services/auth-service/build/libs/auth-service-0.0.1-SNAPSHOT.jar
java -jar services/user-service/build/libs/user-service-0.0.1-SNAPSHOT.jar
java -jar services/chat-service/build/libs/chat-service-0.0.1-SNAPSHOT.jar
java -jar services/history-service/build/libs/history-service-0.0.1-SNAPSHOT.jar
```

#### 방법 2: Docker Compose 사용

```bash
# 도커 이미지 빌드 및 실행
docker-compose -f docker/compose/docker-compose.yml up -d
```

### 프론트엔드 실행

```bash
# 프론트엔드 디렉토리로 이동
cd frontend/web-client

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev

# 프로덕션 빌드
npm run build
```

### API 엔드포인트

기본적으로 서비스는 다음 포트에서 실행됩니다:

- Discovery Service: http://localhost:8761
- API Gateway: http://localhost:8080
- Auth Service: http://localhost:8081
- User Service: http://localhost:8082
- Chat Service: http://localhost:8083
- History Service: http://localhost:8084
- Frontend: http://localhost:3000

## 구현하지 못한 것

[최초 설계](./docs/TROUBLE_SHOOTING.md#아키텍처-설계-kubernetes-기반)에는 고려하여 설계 하였으나 시간과 리소스를 고려하여 아키텍처를 변경하면서 아래의 항목들을 제외하게 되었습니다.

### 프론트엔드 기능

백엔드 개발에 거의 대부분의 시간을 사용하여 제대로 된 기능 구현을 하지 못했습니다.

### 확장성 및 성능

MakeStar 채팅 시스템은 처음부터 확장성을 고려하여 설계되었습니다:

- 각 마이크로서비스는 독립적으로 수평 확장이 가능합니다.
- 메시지 브로커(RabbitMQ/Kafka)를 사용하여 비동기 통신을 구현하였습니다.
- 서비스 디스커버리를 통한 동적 서비스 등록 및 검색이 가능합니다.
- 데이터베이스 샤딩 및 레플리케이션 지원을 고려한 설계입니다.

### 모니터링 및 로깅

시스템 모니터링을 위한 도구와 접근 방식:

- 중앙 집중식 로깅 (ELK 스택)
- 분산 추적 (Spring Cloud Sleuth)
- 성능 메트릭 수집 및 분석 (Prometheus + Grafana)

### 컨테이너화

프로젝트 계획 단계에서는 다음과 같은 컨테이너화 전략을 고려했으나, 시간 제약으로 완전히 구현하지 못했습니다:

- 각 마이크로서비스를 독립적인 Docker 이미지로 패키징
- 경량 베이스 이미지(OpenJDK JRE-11-slim) 사용으로 컨테이너 사이즈 최적화
- 다단계 빌드(Multi-stage build)를 통한 최종 이미지 경량화
- 환경별(개발, 테스트, 운영) 도커 이미지 태그 전략 수립
- Docker Compose를 통한 로컬 개발 환경 구성
- 컨테이너 헬스 체크 및 재시작 정책 설정
- 컨테이너 로그 관리 및 볼륨 마운트 전략

### CI/CD

CI/CD 파이프라인 구축은 다음 단계에서 구현할 예정이었습니다:

- GitHub Actions를 활용한 자동화된 빌드 및 테스트 파이프라인
- 브랜치 기반 배포 전략 (feature → develop → staging → production)
- 테스트 자동화 (단위 테스트, 통합 테스트, E2E 테스트)
- 정적 코드 분석 및 품질 게이트 설정 (SonarQube)
- 자동 버전 관리 및 릴리스 노트 생성
- 컨테이너 레지스트리(Docker Hub, GCR 등)와의 통합
- 블루/그린 배포 전략 구현
- 실패 시 자동 롤백 메커니즘

### 테스트 코드

테스트 코드는 아래와 같은 다층적 테스트 전략으로 계획했으나 현재 구현이 미흡합니다:

- 단위 테스트:
    - 서비스 계층 및 컨트롤러 계층 테스트
    - Mockito를 활용한 의존성 모킹
    - JUnit 5 기반 테스트 케이스 구성
- 통합 테스트:
    - 마이크로서비스 간 API 호출 테스트
    - 데이터베이스 연동 테스트 (Testcontainers 활용)
    - Spring Boot Test 프레임워크 활용
- 성능 테스트:
    - 동시 사용자 부하 테스트 (JMeter)
    - 실시간 메시징 성능 테스트
    - 데이터베이스 쿼리 최적화 검증
- 계약 테스트:
    - Spring Cloud Contract를 활용한 서비스 간 계약 검증
    - Consumer-Driven Contracts 패턴 적용
- E2E 테스트:
    - Selenium/Cypress를 활용한 프론트엔드 자동화 테스트
    - 실제 시나리오 기반 테스트 케이스 구성
    - 사용자 경험 검증 테스트
