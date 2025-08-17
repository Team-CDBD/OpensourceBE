package com.cdbd.opensource.presentation.dto;

public record AiSettingsResponse(
    String apiKeyMasked,
    String maxOutPutTokens,
    Double temperature
) { }
