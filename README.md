# kakao-bank-notification
Spring Kotlin 기반의 외부(슬랙, 텔레그림) 알림 서비스

## Tech Stack
<div>
    <img src="https://img.shields.io/badge/Kotlin-007396?style=flat-square&logo=Kotlin&logoColor=white">
    <img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=Gradle&logoColor=white">
    <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=Spring-Boot&logoColor=white">
</div>

<div>
    <img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat-square&logo=Spring-Data-JPA&logoColor=white">
    <img src="https://img.shields.io/badge/H2-000000?style=flat-square&logo=h2&logoColor=white">
    <img src="https://img.shields.io/badge/Redis-D62124?style=flat-square&logo=Redis&logoColor=white">
</div>

<div>
    <img src="https://img.shields.io/badge/JUnit%205-25A162?style=flat-square&logo=JUnit&logoColor=white">
    <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white">
</div>

### Tool
<div>
    <img src="https://img.shields.io/badge/IntelliJ IDEA-4A154B?style=flat-square&logo=intellijidea&logoColor=white">
    <img src="https://img.shields.io/badge/JMeter-D22128?style=flat-square&logo=Apache-JMeter&logoColor=white">
</div>

## 목차
- [실행방법](#실행방법)
- [시스템 아키텍처](#시스템-아키텍처)
- [ERD](#erd)
- [API 명세서](#api-명세서)

## 실행방법
1. 저장소 클론 [비공개]
    ```bash
    git clone https://github.com/jaydenchuljinlee/kakao-bank-notification
2. 빌드
    ```bash
    ./gradlew build
    ```
3. 애플리케이션 실행
    ```bash
    docker-compose up .
    ```

## 시스템 아키텍처

### 회원
- 로그인 시에 슬랙 SSO 연동한다는 가정
    - 로그인 기능에 대한 요구사항은 없기 때문에 생략
    - 대신, 데이터 초기화 클래스를 통해 Spring Context가 띄워질 때, User 정보와 SSO 정보를 DB, Redis에 저장하도록 설정
        - key는 회원의 id와 연동 서비스 이름을 기반으로
- 회원에 대한 인증/인가는 처리하지 않기 때문에 생략
- OAuth에 대한 인증은 따로 Key 정보를 발급받아 사용해야하기 때문에 생략
- 로그인 API를 통해 로그인을 진행하게 되면 JWT 토큰을 OAuth로 받아온 토큰이라고 가정하고 Redis에 저장
- 토큰 정보는 사용자가 로그아웃한 후에 삭제되는 정보기 때문에 따로 영속화는 하지 않음

### 외부 플랫폼
- [외부 플랫폼] - [플랫폼 채널] - [회원] 관계 구성
- 외부 플랫폼 등록 시에 회원 정보를 Bulk Insert
    - 만약, 사용자가 많아서 시간이 걸리는 작업이라면 배치 서버나 프로시저를 통해 등록
        - 구조가 단순하여 데이터 양만 많을 경우 프로시저 사용
        - 구조가 복잡할 경우, 에러 처리 및 디버깅을 용이하게 하기위해 배치 서버 사용
    - 현재는 두 개의 플랫폼이 등록되어 있고 회원과 플랫폼이 연결되어있다고 가정
        - `domain-data.sql` 파일을 통해 데이터 초기화

### 알림 그룹
- [알림 그룹] - [회원] - [플랫폼 채널] 관계 구성
- 알림 그룹 생성과 알림 그룹에 참여 및 탈퇴 API 제공
- 알림 그룹 중복 생성에 대한 예외 처리 구현

### 알림 전송
- Mockup 서비스 생성
    - Slack, Telegram 등 확장 가능하도록 Interface로 연결
- 외부 서비스 장애 발생 시
    - 저장소에 외부 서비스 장애를 기록
    - 확장을 고려했을 때, 문서형 저장소에 저장하고 싶습니다.
- 외부 서비스 초당 처리 제한
    - 임시 Queue 서버 클래스 생성
        - 초당 처리 제한이 있기 때문에 바로 접근하는 것이 아니라, 이를 중개할 수 있는 서버가 필요
    - Queue 서버 내부에서 Rate Limiting 전략을 통해 안정성있게 처리
        - Queue 서버는 N초에 한 번씩 Task를 실행하면서 Queue에서 M개를 뽑아 처리한다
        - 재시도 처리는 요구사항에서 배제되었기 때문에 Backoff 전략은 제외
- 알림 전송 여부 기록
    - 알림에 대한 대기, 성공, 실패 여부를 통해 추후 재시도 알람을 보낼 수 있도록 기록
    - 예외 발생 시, 예외 내용을 기록

## ERD

![ERD Diagram](/src/main/resources/img/erd_diagram.png)

## API 명세서

### 그룹 생성 API
- **URL**: `/v1/groups?groupName={group_name}`
- **Method**: POST
- **Response**:
    ```json
    {
        "status": 200,
        "message": "그룹을 생성했습니다.",
        "data": null
    }
    ```

### 그룹 사용자 추가 API
- **URL**: `/v1/groups/{group_id}/join?userId={user_id}`
- **Method**: POST
- **Response**:
    ```json
    {
        "status": 200,
        "message": "사용자를 그룹에 추가했습니다.",
        "data": null
    }
    ```

### 그룹 사용자 제거 API
- **URL**: `/v1/groups/{group_id}/leave?userId={user_id}`
- **Method**: POST
- **Response**:
    ```json
    {
        "status": 200,
        "message": "사용자를 그룹에서 제거했습니다.",
        "data": null
    }
    ```

### 알림 전송 API
- **URL**: `/v1/alerts`
- **Method**: POST
- **Request Body**:
    ```json
    {
        "target": [ "@user1", "@@team1", "@all" ],
        "severity": "normal",
        "message": "node1 down"
    }
    ```
- **Response**:
    ```json
    {
        "userCount": "128"
    }
    ```
