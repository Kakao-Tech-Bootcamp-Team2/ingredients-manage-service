# Zipbob Ingredients Manage Service

## 개요 🚀

Zipbob Ingredients Manage Service는 **사용자의 냉장고와 냉장고 내 재료를 효율적으로 관리**하는 서비스입니다. 사용자는 냉장고에 재료를 추가, 수정, 삭제할 수 있으며, 서버에서 제공하는 **기본 재료 목록을 유지 및 관리**할 수 있습니다.

또한, 본 서비스는 **AI 기반 레시피 추천 시스템**과 연계되어 있습니다. 사용자가 특정 재료를 기반으로 레시피를 요청하면, 해당 재료가 사용자의 냉장고에 있는지 확인한 후 **RabbitMQ를 통해 메시지를 전송**합니다. AI 서비스는 이를 바탕으로 레시피를 생성하고 응답을 반환합니다.

<br />

## 주요 기능 🔥

- **냉장고 관리**: 사용자가 회원가입하면 자동으로 자신의 냉장고를 생성
- **재료 관리**: 서버에 존재하는 기본 재료 목록을 유지하고, 사용자가 자신의 냉장고에 있는 재료를 직접 생성, 수정, 삭제 가능
- **AI 서비스 연동**: 사용자 레시피 요청 시, 재료 존재 여부를 확인 후 RabbitMQ를 통해 AI 서비스와 연동
- **비동기 메시지 처리**: RabbitMQ 기반의 메시지 큐를 활용하여 AI 서비스와 효과적으로 데이터 송수신

<br />

## 아키텍처 개요 🏗️

Zipbob Ingredients Manage Service는 **재료 관리와 AI 기반 레시피 추천** 기능을 위해 다음과 같은 흐름을 따릅니다:

1. 사용자가 특정 재료를 기반으로 레시피 요청
2. 서비스가 사용자의 냉장고에서 해당 재료 존재 여부를 확인
3. 확인된 데이터를 RabbitMQ를 통해 메시지로 전송
4. AI 서비스가 메시지를 수신하고 레시피를 생성 후 응답 반환

```
(Client) -> [Edge Service] -> [Ingredients Manage Service] -> [RabbitMQ] -> [AI Service]
```

<br />

## 기술 스택 🛠️

- Java 17
- Spring Boot 3
- Spring cloud
- Spring Data JPA
- MariaDB
- Redis
- RabbitMQ
- Docker

<br />

## 설치 및 실행 방법 💻

### 1. 로컬 실행

```sh
./gradlew build && ./gradlew bootRun
```

### 2. Docker 이미지 빌드

```sh
./gradlew bootBuildImage
```

### 3. Docker 실행

Docker 실행 방법 및 관련 설정에 대한 자세한 내용은 아래 저장소의 README를 참고하세요.

📌 저장소 링크: [Zipbob Deployment Service Repository](https://github.com/Kakao-Tech-Bootcamp-Team2/zipbob-deployment)

<br />

## 메시지 큐(RabbitMQ) 연동 📨

본 서비스는 **RabbitMQ**를 활용하여 AI 서비스와 데이터를 주고받습니다.
재료 확인 후, 다음과 같은 메시지를 생성하여 RabbitMQ로 전송합니다:

```json
[
  {"ingredients":"계란","quantities":"10개"},
  {"ingredients":"우유","quantities":"10리터"},
  {"ingredients":"소고기","quantities":"10그램"}
]
```

AI 서비스는 해당 데이터를 분석하여 레시피를 생성한 후 응답을 반환합니다.

<br />

## 데이터베이스 전략 📊

Zipbob Ingredients Manage Service는 **마스터-슬레이브 전략**을 활용하여 데이터베이스 부하를 분산하고 가용성을 높입니다. 🏆

- **마스터 노드**는 모든 **쓰기 작업**을 담당합니다.
- **슬레이브 노드**는 **읽기 전용 작업**을 수행하여 시스템 부하를 분산합니다.
- 대량의 요청이 발생하더라도 **안정적인 성능**을 유지할 수 있습니다.

### 추가 데이터베이스 최적화

- **사용자의 냉장고 및 재료 정보는 MariaDB에 저장**
- **Redis를 활용하여 캐싱 및 빠른 조회 기능 제공**
- **RabbitMQ 기반 비동기 메시지 처리로 AI 서비스와 원활한 연동**
