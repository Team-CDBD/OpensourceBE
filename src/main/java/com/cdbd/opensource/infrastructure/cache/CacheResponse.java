package com.cdbd.opensource.infrastructure.cache;

import lombok.Builder;

import java.util.List;

@Builder
public record CacheResponse(
        String className,
        String method,
        int line,
        String message,
        String severity,
        List<String> futureCalls,
        String result
) {
}
