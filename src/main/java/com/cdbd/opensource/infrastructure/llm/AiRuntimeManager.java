package com.cdbd.opensource.infrastructure.llm;

import com.cdbd.opensource.infrastructure.llm.AiDefaultsProperties.Options;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(AiDefaultsProperties.class)
public class AiRuntimeManager {

  private final AtomicReference<AiRuntimeSettings> settingsRef = new AtomicReference<>();
  private final AtomicReference<ChatClient> clientRef = new AtomicReference<>();

  public AiRuntimeManager(AiDefaultsProperties aiDefaultsProperties) {
    AiDefaultsProperties.Options opt = aiDefaultsProperties.chat().options();
    apply(new AiRuntimeSettings(
        aiDefaultsProperties.baseUrl(),
        aiDefaultsProperties.apiKey(),
        opt.model(),
        opt.maxTokens() ,
        opt.temperature(),
        opt.presencePenalty(),
        opt.frequencyPenalty()
    ));
  }

  public AiRuntimeSettings current() { return settingsRef.get(); }

  public ChatClient client() { return clientRef.get(); }

  private void apply(AiRuntimeSettings s) {
    OpenAiApi api = OpenAiApi.builder().baseUrl(s.baseUrl()).apiKey(s.apiKey()).build();
    OpenAiChatOptions options = OpenAiChatOptions.builder()
        .model(s.model())
        .temperature(s.temperature())
        .maxTokens(s.maxTokens())
        .presencePenalty(s.presencePenalty())
        .frequencyPenalty(s.frequencyPenalty())
        .build();

    OpenAiChatModel model = OpenAiChatModel.builder().openAiApi(api).defaultOptions(options).build();
    ChatClient newClient = ChatClient.builder(model)
        .defaultOptions(options)
        .build();

    clientRef.set(newClient);
    settingsRef.set(s);
  }
}
