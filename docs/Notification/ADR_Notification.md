# ADR : Notification\_박희재

## 작성일

2025-07-20

---

## 컨텍스트

* 시스템 전반에서 장애, 상태 변화 등의 이벤트가 발생했을 때, 운영자 또는 사용자에게 다양한 채널을 통해 **알림을 전송**할 수 있어야 한다.
* 채널은 메일, 디스코드, 슬랙 등으로 구성되며, 향후 추가될 수 있다.
* 비동기적으로 알림을 전송하고, 실패 시 **자동 재시도**할 수 있는 구조가 요구된다.
* 알림 대상 채널은 **DB 테이블에 저장**된다.
* 이 구조는 높은 유연성과 확장성을 요구하며, 구현 복잡도 및 유지보수성도 고려 대상이다.

---

## 결정

* 우리는 Notification 도메인을 중심으로, 이벤트 기반 알림 전송 구조를 채택한다.
* `NotificationService`가 알림을 발행하면, 이벤트 리스너가 해당 알림을 처리하고, 전송 실패 시 상태값과 재시도 횟수를 갱신한 뒤, 동일 이벤트를 다시 발행하여 재시도한다.
* 채널 설정은 DB 테이블 `channel_setting`에 저장된다.
* 서버 기동 시 `ChannelSettingManager`에서 DB로부터 설정을 로드하여 인메모리 캐시에 저장한다.
* 관리자 페이지에서 수정이 발생하면 DB를 업데이트하고, `ChannelSettingManager.reload()`를 호출하여 캐시를 갱신한다.
* `NotificationSender`는 채널별 전송을 처리하는 단일 인터페이스이며, 내부적으로 설정 리스트를 순회하며 전송을 시도한다.

### DB 테이블 설계

```sql
-- PostgreSQL 기준 채널 설정 테이블
CREATE TABLE channel_setting (
    id           BIGSERIAL PRIMARY KEY,
    channel_type VARCHAR(20)  NOT NULL,
    webhook_url  TEXT,
    token        TEXT,
    from_address VARCHAR(100),
    to_address   VARCHAR(100),
    username     VARCHAR(50),
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT now()
);
```

### 시퀀스 다이어그램

```mermaid
sequenceDiagram
    participant AdminUI as 관리자페이지
    participant KafkaConsumer as 카프카 컨슈머
    participant NotificationService as NotificationService
    participant ChannelSettingManager as 채널설정매니저
    participant EventPublisher as 이벤트발행자
    participant EventListener as @Async 이벤트리스너
    participant NotificationSender as NotificationSender

    AdminUI->>ChannelSettingManager: DB에 채널 설정 저장
    ChannelSettingManager->>ChannelSettingManager: 설정 적용 및 캐시 갱신 (DB 로드)
    KafkaConsumer->>NotificationService: 알림 데이터 전달
    NotificationService->>ChannelSettingManager: DB에서 채널 설정 조회
    NotificationService->>EventPublisher: NotificationSendEvent 발행

    EventPublisher-->>EventListener: NotificationSendEvent 전달
    EventListener->>NotificationSender: send(notification)
    loop 채널 목록 순회
        NotificationSender->>NotificationSender: 채널별 전송 시도
        alt 전송 성공
            NotificationSender->>Notification: 상태 = SENT
        else 전송 실패
            NotificationSender->>Notification: retryCount++, 상태 = RETRYING
            NotificationSender->>EventPublisher: NotificationSendEvent 재발행
        end
    end
```

### 도메인 클래스 다이어그램

```mermaid
classDiagram
    direction LR

    class Notification {
        - title: String
        - body: String
        - recipient: String
        - status: NotificationStatus
        - channels: List~NotificationChannelType~
        - retryCount: int
    }

    class NotificationStatus {
        <<enum>>
        READY
        SENT
        FAILED
        RETRYING
    }

    class NotificationChannelType {
        <<enum>>
        DISCORD
        MAIL
        SLACK
    }

    class NotificationSender {
        + send(notification: Notification): Result
    }

    class EmailSender {
        + send(notification: Notification): Result
    }

    class SlackSender {
        + send(notification: Notification): Result
    }

    class DiscordSender {
        + send(notification: Notification): Result
    }

    Notification --> NotificationStatus : 상태값
    Notification --> NotificationChannelType : 채널

    NotificationSender <|-- EmailSender
    NotificationSender <|-- SlackSender
    NotificationSender <|-- DiscordSender
```

---

## 결과

* 비동기 이벤트 기반 처리로 서비스 로직과 전송 로직을 분리 → **관심사 분리** 달성
* DB 설정 기반으로 채널 동적 구성 가능 → **확장성** 확보
* 실패 시 상태 기반 재시도 가능 → **내결함성** 향상
* 단일 인터페이스 구조로 채널 통합 관리 → **유지보수성** 향상
* 관리자 페이지에서 DB 설정을 업데이트하고 캐시를 갱신하여 Hot reload 지원.

---

## 대안

| 대안                     | 장점                  | 단점                | 채택 여부 |
| ---------------------- | ------------------- | ----------------- | ----- |
| 동기 방식 전송               | 구조 단순               | 장애 시 전체 중단        | X     |
| Kafka에 직접 전송 명령 메시지 전파 | 완전 분리, 확장성 좋음       | 도메인 경계 흐림         | X     |
| DB에 채널 설정 저장           | Hot reload 가능       | 복잡도 상승, IO 부담     | O     |
| JSON 기반 캐시 + 이벤트 구조    | 유연성, 분리도, 유지보수성 뛰어남 | 구현 시 reload 설계 필요 | X     |

---

## 담당자

* 작성자: 박희재
* 검토자: 최현호

---
