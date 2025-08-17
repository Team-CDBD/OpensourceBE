package com.cdbd.opensource.presentation.dto;

public record AiSettingsResponse(
    String apiKeyMasked,
    Integer maxOutPutTokens,
    Double temperature
) { }
