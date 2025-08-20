# ADR : 단순 LLM 연동 구조_서예진

## 작성일

2025-07-17

## 컨텍스트

- 배경
    - 서비스 내 여러 기능에서 커스텀 어노테이션이 적용된 메서드는 정통적으로 Kafka를 통해 로그를 전송하고 있습니다.
    - 기존에는 로그가 실시간으로 로그 DB에 적재된다고 가정했으나, 실제로는 Kafka에 적재되면 바로 서버에 전달되는 구조입니다.
- 문제 상황
    - 기존 MCP 아키텍처 설계는 로그 DB 조회를 전제로 했으나, 실제 로그 흐름은 Kafka → 서버 직접 전달 방식입니다.
    - 복잡한 Tool 구조로 인해 불필요한 오버헤드가 발생하고 있습니다.
- 요구 사항 : 커스텀 어노테이션이 적용된 메서드의 로그를 기반으로 LLM이 원인 분석 및 해결 방안을 제시해주는 기능이 필요합니다.
    1. 커스텀 어노테이션이 적용된 메서드에서 에러 발생 시 자동 분석 트리거
    2. Kafka를 통해 전달받은 로그 직접 분석
    3. LLM 기반 원인 분석 및 해결 방안 제시
    4. 분석 결과 저장(캐시, 로그 DB)

## 결정

- 기존 MCP + Tool 구조를 단순화하여 **단순 LLM 호출 구조**로 변경하기로 했습니다.
- MCP Server에서 Kafka로부터 전달받은 로그를 직접 LLM에 전달하여 분석을 수행합니다.
- 시퀀스 다이어그램

    ```mermaid
    sequenceDiagram
        participant Client as 클라이언트 서비스
        participant Annotation as 커스텀 어노테이션<br/>(@LogAnalysis)
        participant Kafka as Kafka
        participant MCP as MCP Server
        participant Config as 설정 서비스<br/>(ChatClient Config)
        participant LLM as LLM API<br/>(GPT-4, Claude)
        participant Cache as 분석 결과<br/>캐시
        participant LogDB as 로그DB
        participant Dashboard as 대시보드<br/>/알림
        
        Note over Client, Dashboard: 분석 플로우
        Client->>Annotation: 메서드 호출 (에러 발생)
        Annotation->>Kafka: 에러 로그 전송
        Kafka->>MCP: 에러 로그 수신
    
        MCP->>Config: 사용자 LLM 모델 설정 조회
        Config-->>MCP: 선택된 LLM 모델 정보 반환<br/>(GPT-4/Claude/etc)
    
        MCP->>MCP: 로그 컨텍스트 구성<br/>(에러 정보 정리)
    
        MCP->>LLM: 분석 요청<br/>(에러 로그 + 선택된 모델)
        LLM-->>MCP: 원인 분석 및<br/>해결방안 반환
    
        MCP->>Cache: 분석 결과 저장
        MCP->>LogDB: 분석 결과 저장
        MCP->>Dashboard: 분석 결과 전송
        Dashboard->>Client: 알림 발송<br/>(Slack, Email)
    
        Note over Client, Dashboard: 에러 처리
        alt LLM API 장애
            MCP->>LLM: 분석 요청 (실패)
            MCP->>Config: Fallback LLM 모델 설정 조회
            Config-->>MCP: Fallback 모델 정보 반환
            MCP->>LLM: 재시도 요청 (Fallback 모델)
            LLM-->>MCP: 분석 결과 반환
        end
    ```


## 결과

- 긍정적 결과
    - 구조 단순화 : 복잡한 Tool 레이어 제거로 아키텍처가 간소화되었습니다.
    - 실시간 분석 : DB 조회 없이 Kafka에서 직접 받은 로그를 즉시 분석할 수 있습니다.
    - 운영 복잡도 감소
    - 응답 속도 향상 : DB 조회 과정 생략으로 분석 응답 시간이 단축되었습니다.
- 부정적 결과
    - 컨텍스트 제한 : 단일 에러 로그만으로 분석하므로 이전 로그 기반 맥락 분석이 어려울 수 있습니다.

## 대안 : MCP 구조

- 설명 : 로그 DB 조회를 통해 시간 범위 기반 관련 로그를 수집하고 Tool에서 분석하는 구조
- 선택하지 않은 이유
    - Kafka에서 로그를 ElasticSearch에 바로 저장할 시 도입하려고 합니다.
    - 현재 프로젝트 상황에 있어서는 구조적 복잡도 대비 실질적 이득이 제한적입니다.

## 담당자

- 작성자 : 서예진