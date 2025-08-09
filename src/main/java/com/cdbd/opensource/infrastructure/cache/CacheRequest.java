package com.cdbd.opensource.infrastructure.cache;

import lombok.Builder;

import java.time.Duration;
import java.util.List;

@Builder
public record CacheRequest(
        String className,
        String method,
        int line,
        String message,
        String severity,
        List<String> futureCalls,
        Duration ttl
) {
}
