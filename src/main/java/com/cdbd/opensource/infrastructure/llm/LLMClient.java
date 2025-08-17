package com.cdbd.opensource.infrastructure.llm;

import com.cdbd.opensource.domain.EventLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

@Component
public class LLMClient {

  private final ChatClient chatClient;
  private final ObjectMapper objectMapper;
  private final PromptTemplate promptTemplate;

  public LLMClient(
      ChatClient chatClient,
      ObjectMapper objectMapper,
      @Value("classpath:/prompts/analyze-event-log.txt") Resource promptResource) {
    this.chatClient = chatClient;
    this.objectMapper = objectMapper;
    this.promptTemplate = new PromptTemplate(promptResource);
  }

  public LLMResult analyzeEventLog(EventLog eventLog) {
    try {
      String userMessage = objectMapper.writeValueAsString(eventLog);
      Prompt prompt = promptTemplate.create(Map.of("userMessage", userMessage));
      String result = chatClient.prompt(prompt).call().content();
      return new LLMResult(result);

    } catch (JsonProcessingException e) {
      throw new RuntimeException("EventLog를 JSON으로 변환하는 데 실패했습니다.", e);
    }
  }
}
