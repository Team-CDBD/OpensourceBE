# ADR : MCP 기반 아키텍처 도입 결정_서예진_보류

## 작성일

2025-07-16

## 컨텍스트

- 배경
    - 서비스 내 여러 기능에서 커스텀 어노테이션(`@LogAnalysis`, `@ErrorTracking` 등)이 적용된 메서드는 공통적으로 Kafka를 통해 로그를 전송하고 있습니다.
    - 로그는 실시간으로 로그 DB에 적재되고 있습니다.
- 문제 상황
    - 단순히 에러 로그를 LLM에 그대로 전달하는 방식은 정황 정보 부족, 분석 결과의 일관성 저하, 분석 비용 낭비 등의 한계가 있습니다.
- 요구 사항 : 커스텀 어노테이션이 적용된 메서드에서 에러가 발생한 경우, 해당 요청 시점과 관련된 최근 로그(특히 실패 로그)를 기반으로 LLM이 원인 분석 및 해결 방안을 제시해주는 기능이 필요합니다.
    1. 커스텀 어노테이션이 적용된 메서드에서 에러 발생 시 자동 분석 트리거
    2. 에러 발생 시점 기준 전후 N분간의 관련 로그 수집 및 분석
    3. LLM 기반 원인 분석 및 해결 방안 제시
    4. 분석 결과 저장

### 결정

- LLM 호출 구조를 단순화 하지 않고, MCP(Model Context Protocol) 아키텍처를 도입하기로 했습니다.
- MCP Server는 로그 적재가 완료된 이후 DB에서 로그를 조회하여 분석을 수행합니다.
- 실제 분석은 Tool 내부에서 수행되며, 이 Tool은 다음의 흐름을 포함합니다.
    1. 주어진 context(에러 정보, 타임스탬프 등)를 기반으로 로그 DB 조회
    2. 조회된 로그를 기반으로 LLM 분석 요청
    3. 분석 결과를 저장하고 반환

이 설계를 통해, MCP Server는 단순 트리거 역할만 담당하며, 실제 분석 로직은 Tool 내부로 캡슐화됩니다.

- 시퀀스 다이어그램
    
    ```mermaid
    sequenceDiagram
        participant Client as 클라이언트 서비스
        participant Annotation as 커스텀 어노테이션<br/>(@LogAnalysis)
        participant Kafka as Kafka
        participant LogDB as 로그 DB<br/>(Elasticsearch)
        participant MCP as MCP Server
        participant Tool as Analysis Tool
        participant Config as 설정 서비스<br/>(ChatClient Config)
        participant LLM as LLM API<br/>(GPT-4, Claude)
        participant Cache as 분석 결과<br/>캐시
        participant Dashboard as 대시보드<br/>/알림
    
        Note over Client, Dashboard: 정상 플로우
        Client->>Annotation: 메서드 호출
        Annotation->>Kafka: 로그 전송
        Kafka->>LogDB: 로그 적재
        
        Note over Client, Dashboard: 에러 발생 시 분석 플로우
        Client->>Annotation: 메서드 호출 (에러 발생)
        Annotation->>Kafka: 에러 로그 전송
        Kafka->>LogDB: 에러 로그 적재
        
        Annotation->>MCP: 에러 이벤트 트리거<br/>(타임스탬프, 에러 정보)
        
        MCP->>Tool: 분석 요청<br/>(context 전달)
        
        Tool->>Config: 사용자 LLM 모델 설정 조회<br/>(ChatClient 설정)
        Config-->>Tool: 선택된 LLM 모델 정보 반환<br/>(GPT-4/Claude/etc)
        
        Tool->>LogDB: 관련 로그 조회<br/>(시간 범위 기반)
        LogDB-->>Tool: 로그 데이터 반환
        
        Tool->>Tool: 컨텍스트 구성<br/>(로그 정렬, 필터링)
        
        Tool->>LLM: 분석 요청<br/>(구성된 컨텍스트 + 선택된 모델)
        LLM-->>Tool: 원인 분석 및<br/>해결방안 반환
        
        Tool->>Cache: 분석 결과 저장
        Tool->>MCP: 분석 결과 반환
        
        MCP->>Dashboard: 분석 결과 전송
        Dashboard->>Client: 알림 발송<br/>(Slack, Email)
        
        Note over Client, Dashboard: 에러 처리
        alt LLM API 장애
            Tool->>LLM: 분석 요청 (실패)
            Tool->>Config: Fallback LLM 모델 설정 조회
            Config-->>Tool: Fallback 모델 정보 반환
            Tool->>LLM: 재시도 요청 (Fallback 모델)
            LLM-->>Tool: 분석 결과 반환
        end
        
        alt 로그 DB 장애
            Tool->>LogDB: 로그 조회 (실패)
            Tool->>Tool: 기본 분석 수행<br/>(에러 메시지만으로)
            Tool->>MCP: 제한적 분석 결과 반환
        end
    ```
    

### 결과

- 긍정적 결과
    - 정황 기반 자동 분석 : 단일 LLM 호출로는 처리 어려운 로그 기반 맥락 분석이 가능해졌습니다.
    - 책임 분리와 유연성 확보 : MCP Server는 메세지 트리거만 담당하고, 핵심 로직은 Tool에서 관리됩니다.
- 부정적 결과
    - 구조 복잡도 증가
    - 운영 이해도 필요
    - 정확도 한계 : 사용자의 커스텀 어노테이션 사용 패턴에 따른 분석 정확도 편차

### 대안 : 단순 LLM 호출

- 설명 : 에러 내용을 직접 LLM에게 전송하여 분석 결과를 받는 구조
- 선택하지 않은 이유 :
    - 로그 DB 와의 연계가 없어 분석 정확도 및 신뢰성 떨어짐

### 담당자

- 작성자 : 서예진

### 위 문서에서 나온 MCP 구조는 적용하지 않기로한다.(참고 : ADR_단순 LLM 연동 구조)