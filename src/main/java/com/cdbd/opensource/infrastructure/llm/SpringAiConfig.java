package com.cdbd.opensource.infrastructure.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAiConfig {

  @Bean
  public ChatClient chatClient(ChatModel chatModel) {
    return ChatClient.create(chatModel);
  }

}
