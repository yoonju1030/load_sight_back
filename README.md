# LoadSight Server

부하 테스트 시뮬레이터의 Control API 서버입니다. 단일 API에 대해 부하를 발생시키고, 지연·에러를 실시간으로 보고, 실행 결과를 리포트로 제공합니다.

## 주요 기능

- **테스트 생성 및 관리**: 부하 테스트 설정 생성, 조회, 삭제
- **테스트 실행 제어**: 테스트 시작, 중지, 상태 조회
- **실시간 메트릭**: Server-Sent Events (SSE)를 통한 실시간 성능 지표 전송
- **결과 리포트**: 테스트 완료 후 상세 리포트 제공

## 기술 스택

- Java 17
- Spring Boot 4.0.2
- Lombok
- Java HTTP Client (java.net.http)

## API 엔드포인트

### 테스트 관리

#### 테스트 생성
```http
POST /api/tests
Content-Type: application/json

{
  "name": "API 부하 테스트",
  "targetUrl": "https://api.example.com/endpoint",
  "method": "GET",
  "headers": {
    "Authorization": "Bearer token"
  },
  "body": null,
  "threads": 10,
  "duration": 60,
  "rampUp": 10
}
```

#### 테스트 목록 조회
```http
GET /api/tests
```

#### 테스트 조회
```http
GET /api/tests/{testId}
```

#### 테스트 삭제
```http
DELETE /api/tests/{testId}
```

### 테스트 실행 제어

#### 테스트 시작
```http
POST /api/tests/{testId}/start
```

#### 테스트 중지
```http
POST /api/tests/{testId}/stop
```

#### 테스트 상태 조회
```http
GET /api/tests/{testId}/status
```

응답 예시:
```json
{
  "executionId": "uuid",
  "status": "RUNNING",
  "startedAt": "2024-01-01T10:00:00",
  "totalRequests": 1000,
  "successfulRequests": 950,
  "failedRequests": 50,
  "averageResponseTime": 150.5,
  "minResponseTime": 50.0,
  "maxResponseTime": 500.0,
  "p95ResponseTime": 300.0,
  "p99ResponseTime": 450.0,
  "requestsPerSecond": 16.67,
  "errorRate": 5.0,
  "progress": 50
}
```

### 메트릭

#### 메트릭 조회
```http
GET /api/metrics/{testId}
```

#### 실시간 메트릭 스트림 (SSE)
```http
GET /api/metrics/{testId}/stream
Accept: text/event-stream
```

### 리포트

#### 리포트 조회
```http
GET /api/reports/{testId}
```

## 실행 방법

### 빌드
```bash
./gradlew build
```

### 실행
```bash
./gradlew bootRun
```

또는

```bash
java -jar build/libs/loadsightserver-0.0.1-SNAPSHOT.jar
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

## 설정

`src/main/resources/application.properties` 파일에서 설정을 변경할 수 있습니다:

```properties
server.port=8080
spring.application.name=loadsightserver
```

## 프론트엔드 연동

프론트엔드 저장소: https://github.com/yoonju1030/load_sight_front

프론트엔드에서 이 API를 호출하여 부하 테스트를 생성하고 실행할 수 있습니다.

## 아키텍처

```
┌─────────────┐
│   Web UI    │ (프론트엔드)
└──────┬──────┘
       │ HTTP/REST
       │
┌──────▼──────────────┐
│   Control API       │ (이 프로젝트)
│   - 테스트 관리      │
│   - 실행 제어        │
│   - 메트릭 수집      │
└──────┬──────────────┘
       │
       │ (향후 확장)
       │
┌──────▼──────────────┐
│   Load Agent        │
│   - HTTP 트래픽 생성 │
└─────────────────────┘
```

## 주의사항

- 현재는 메모리 기반 저장소를 사용합니다. 프로덕션 환경에서는 데이터베이스 연동이 필요합니다.
- 대규모 부하 테스트의 경우 별도의 Load Agent 컴포넌트로 분리하는 것을 권장합니다.
- 동시 실행 가능한 테스트 수는 서버 리소스에 따라 제한될 수 있습니다.

