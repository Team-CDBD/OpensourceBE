package com.cdbd.opensource.infrastructure.cache;

import lombok.Builder;

import java.util.List;

@Builder
public record CacheData(
        String message,
        String severity,
        List<String> futureCalls
) {
    public static CacheData from (CacheRequest request) {
        return CacheData.builder()
                .message(request.message())
                .severity(request.severity())
                .futureCalls(request.futureCalls())
                .build();
    }
}
