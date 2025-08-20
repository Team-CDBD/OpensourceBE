package com.cdbd.opensource.presentation.dto;

public record AiSettingsRequest(
    String apiKey,
    Integer maxOutputTokens,
    Double temperature
) { }
