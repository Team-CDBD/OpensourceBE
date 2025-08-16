package com.cdbd.opensource.infrastructure.llm;

import com.cdbd.opensource.domain.EventLog;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class LLMClient {

  private final ChatClient chatClient;

  public LLMClient(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  public LLMResult analyzeEventLog(EventLog eventLog) {
    String systemMessage = "";

    String result = chatClient
        .prompt()
        .system(systemMessage)
        .user(eventLog.toString())
        .call()
        .content();

    return new LLMResult(result);
  }
}
