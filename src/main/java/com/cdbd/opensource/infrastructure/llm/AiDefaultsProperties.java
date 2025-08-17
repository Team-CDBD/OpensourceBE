package com.cdbd.opensource.infrastructure.llm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.ai.openai")
public record AiDefaultsProperties(
    String baseUrl,
    String apiKey,
    Chat chat
) {

  public record Chat(Options options) { }

  public record Options(
      String model,
      Integer maxTokens,
      Double temperature,
      Double presencePenalty,
      Double frequencyPenalty
  ) { }
}
