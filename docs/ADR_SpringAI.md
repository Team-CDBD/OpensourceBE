# ADR : LLM 연동을 위한 Spring AI 프레임워크 도입_서예진

## 작성일

2025-07-16

## 컨텍스트

- 배경 : LLM 기반 분석 기능을 도입해야 합니다. 이를 위해서는 우리 애플리케이션과 OpenAI, Google Gemini 등 외부 LLM 서비스 간의 안정적이고 효율적인 통신 방법이 필요합니다.
- 문제 상황 : LLM 연동을 위해 `RestTemplate` 이나 `WebClient` 를 사용하여 HTTP API를 직접 호출하고 응답 JSON을 수동으로 파싱하는 방식을 고려할 수 있습니다. 하지만 이 방식은 특정 LLM(예: OpenAI)의 API 명세에 코드가 강하게 종속되고, 모델 변경이나 추가 시 많은 코드를 수정해야 하는 문제를 야기합니다. 또한, 반복적이고 보일러플레이트 코드가 많아져 생산성을 저해합니다.
- 요구사항 : 다양한 LLM 모델을 유연하게 교체하고 확장할 수 있는 추상화된 개발 환경이 필요합니다. 또한, 요청/응답 및 예외 처리, 재시도 등을 일관적으로 지원해야합니다.

## 결정

- LLM 연동을 위해 `Spring AI` 프레임워크를 도입할 것입니다.
- `Spring AI`는 `ChatClient` 를 통해 다양한 AI 모델을 위한 일관된 API 추상화를 제공합니다. 이를 통해 개발자는 특정 모델의 API 구현 세부 사항에 얽매이지 않고, 비즈니스 로직에 더 집중할 수 있습니다.
- 또한, Tool Calling, MCP 까지 동일 계층에서 확장할 수 있습니다.
- 예시 코드
    - Spring AI 도입 전 (WebClient 직접 사용)
        
        ```java
        //OpenAI
        class OpenAiRequest { ... }
        class OpenAiResponse { ... }
        
        @Service
        public class OpenAiChatService {
        
            private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
        
            public String chatWithOpenAi(String message) {
                //OpenAiRequest 생성, 헤더 설정, API 호출, 결과 파싱
                return response.getChoices().get(0).getMessage().getContent();
            }
        }
        
        //Gemini
        class GeminiRequest { ... }
        class GeminiResponse { ... }
        class Content { ... }
        class Part { ... }
        
        @Service
        public class GeminiChatService {
        
            private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
        
            public String chatWithGemini(String message) {
                String apiKey = System.getenv("GEMINI_API_KEY");
        
                GeminiRequest request = new GeminiRequest(new Content[]{ ... });
        
                GeminiResponse response = webClientBuilder.build()
                        .post()
                        .uri(uriBuilder -> uriBuilder.path(GEMINI_URL).queryParam("key", apiKey).build())
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(GeminiResponse.class)
                        .block();
        
                return response.getCandidates()[0].getContent().getParts()[0].getText();
            }
        }
        ```
        
        문제점 : 모델을 GPT에서 Gemini로 바꾸려면, 대부분의 코드를 처음부터 다시 작성해야 합니다.
        
    - Spring AI 도입 후 (ChatClient 사용)
        
        Spring AI를 사용하면 모든 복잡성이 프레임워크 내부로 감춰집니다.
        
        ```java
        import org.springframework.ai.chat.client.ChatClient;
        import org.springframework.stereotype.Service;
        
        @Service
        public class ChatService {
        
            private final ChatClient chatClient;
        
            public NewChatService(ChatClient.Builder builder) {
                this.chatClient = builder.build();
            }
        
            public String chat(String message) {
                return chatClient.prompt()
                        .user(message)
                        .call()
                        .content();
            }
        }
        ```
        
        장점 : 모델을 변경하고 싶다면, `application.yml` 설정 파일만 수정하면 되고 위의 코드는 바꿀 필요가 없습니다.
        
- **시퀀스 다이어그램** (예시 : “Hello”)
    - ChatService 호출 부분을 User로 표현
    
    ```mermaid
    sequenceDiagram
        participant User
        participant ChatService
        participant ChatClient
        participant LLM
        User->>ChatService: chat("Hello")
        ChatService->>ChatClient: prompt().user("Hello").call()
        ChatClient->>LLM: HTTP 요청
        LLM-->>ChatClient: 응답(JSON)
        ChatClient-->>ChatService: 응답 파싱 및 .content()
        ChatService-->>User: "Hi there!"
    ```
    
- **컴포넌트 다이어그램**
    
    Spring AI Framework 안에 MCP 관련 내용은 확장 가능을 표시하였습니다.
    
    ```mermaid
    flowchart TB
        subgraph Application
          ChatService
          ChatClient
        end
    
        subgraph Spring AI Framework
          ChatClientBuilder --> ChatClient
          ToolCallingManager
          MCPClient
          MCPServer
        end
    
        subgraph LLM Providers
          OpenAI
          Gemini
          Ollama
        end
    
        ChatService --> ChatClient
        ChatClient --> OpenAI
        ChatClient --> Gemini
        ChatClient --> Ollama
    
        ChatClient --> ToolCallingManager
        ChatClient --> MCPClient
    
        MCPClient --> MCPServer
        MCPServer --> ToolCallingManager 
    
    ```
    

## 결과

- 긍정적 결과 :
    - **모델 유연성 확보** : `@Bean` 설정만으로 OpenAI, Ollama, Gemini 등 사용자가 선택한 LLM 모델로 즉시 전환할 수 있어, 특정 벤더에 종속되지 않고 유연하게 대응할 수 있습니다.
    - **개발 생산성 향상** : LLM 요청/응답 처리를 위한 반복적인 코드 작성이 사라집니다. `ChatClient` 와 같은 직관적인 인터페이스를 통해 몇 줄의 코드 만으로 LLM 호출이 가능해져 개발 속도가 크게 향상됩니다.
    - **Spring 생태계와의 완벽한 통합 :** 기존 Spring Boot 애플리케이션에 자연스럽게 통합됩니다.
- 부정적 결과 (Trade-offs) :
    - **신규 라이브러리 의존성** : 아직 v1.0.0 의 프레임 워크에 의존성이 프로젝트에 추가되어 특정 프레임워크에 의존하게 됩니다.
    - **학습 곡선** : 팀원들이 `Spring AI`의 핵심 개념(예: `ChatClient`, `PromptTemplate` 등)을 익히는 데 초기 학습 시간이 필요합니다.

## **대안**

- **대안:** `RestTemplate`/`WebClient`를 이용한 직접 API 호출
    - **장점:** 추가 라이브러리 없이 Spring의 기본 기능만으로 구현할 수 있습니다.
    - **선택하지 않은 이유:** LLM 모델 제공업체가 변경되거나 API 명세가 업데이트될 때마다 관련된 모든 코드를 직접 수정해야 합니다. 이는 유지보수 비용을 급격히 증가시키고, 변화에 대응하기 어렵게 만듭니다.

## 담당자

서예진
