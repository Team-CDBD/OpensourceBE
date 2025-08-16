package com.cdbd.opensource.infrastructure.llm;

import com.cdbd.opensource.domain.EventLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class LLMClient {

  private final ChatClient chatClient;
  private final ObjectMapper objectMapper;

  public LLMClient(ChatClient chatClient, ObjectMapper objectMapper) {
    this.chatClient = chatClient;
    this.objectMapper = objectMapper;
  }

  public LLMResult analyzeEventLog(EventLog eventLog) {
    String systemMessage = """
        너는 숙련된 Java 시니어 개발자이자 소프트웨어 아키텍트이다. 주어진 에러 로그를 분석하여, 문제의 근본 원인을 진단하고 명확한 해결책을 제시하는 역할을 수행한다.
        **지시사항(Instructions):**
        사용자는 `EventLog`라는 이름의 JSON 객체를 제공할 것이다. 이 로그를 분석하여 문제의 **원인**, **영향**, 그리고 **해결 방안**을 구체적으로 제시해야 한다.
        **입력 데이터 형식 (Input Data Format):**
        사용자가 제공할 JSON 객체는 다음과 같은 필드를 포함한다.
        - `className`: 오류가 발생한 클래스의 전체 경로
        - `method`: 오류가 발생한 메소드 이름
        - `line`: 오류가 발생한 소스 코드의 라인 번호
        - `message`: 발생한 예외(Exception)의 메시지
        - `severity`: 오류의 심각도
        - `futureCalls`: 예외가 발생한 지점 이후에 호출될 예정이었던 메소드 목록

        **출력 형식 (Output Format):**
        분석 결과는 반드시 아래의 세 가지 항목으로 나누어 명확하고 구조화된 형식으로 제공해야 한다.

        ### 1. 원인 분석
        `message`와 `className`, `method` 필드를 종합하여 예외의 가장 가능성 높은 근본 원인을 기술하라.

        ### 2. 영향 분석
        `futureCalls` 목록을 핵심 단서로 활용하여, 이 오류로 인해 어떤 중요한 비즈니스 로직이나 작업이 중단되었는지 설명하라.

        ### 3. 해결 방안 제안
        문제를 해결하기 위한 구체적이고 실행 가능한 코드 수정안을 제시하라. 방어적인 코드(예: null 체크)나 올바른 로직을 포함한 코드 예시를 제공하여 개발자가 바로 적용할 수 있도록 도와라.
        """;
    try {
      String userMessage = objectMapper.writeValueAsString(eventLog);
      String result = chatClient
          .prompt()
          .system(systemMessage)
          .user(userMessage)
          .call()
          .content();

      return new LLMResult(result);

    } catch (JsonProcessingException e) {
      throw new RuntimeException("EventLog를 JSON으로 변환하는 데 실패했습니다.", e);
    }
  }
}
