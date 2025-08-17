package com.cdbd.opensource.infrastructure.llm;

public record AiRuntimeSettings(
    String baseUrl,
    String apiKey,
    String model,
    Integer maxTokens,
    Double temperature,
    Double presencePenalty,
    Double frequencyPenalty
) {
  public AiRuntimeSettings mergeUserInputs(
      String newApiKey, Integer newMaxTokens, Double newTemperature) {
    return new AiRuntimeSettings(
        baseUrl,
        (newApiKey != null && !newApiKey.isBlank()) ? newApiKey : apiKey,
        model,
        newMaxTokens != null ? newMaxTokens : maxTokens,
        newTemperature != null ? newTemperature : temperature,
        presencePenalty,
        frequencyPenalty
    );
  }

  public String maskedApiKey() {
    if (apiKey == null || apiKey.isBlank()) return "";
    return "****" + apiKey.substring(Math.max(0, apiKey.length() - 4));
  }

  @Override public String toString() {
    return "AiRuntimeSettings{baseUrl=%s, apiKey=****, model=%s, maxTokens=%s, temperature=%s, presencePenalty=%s, frequencyPenalty=%s}"
        .formatted(baseUrl, model, maxTokens, temperature, presencePenalty, frequencyPenalty);
  }
}
