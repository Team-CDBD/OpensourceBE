package com.cdbd.opensource.infrastructure.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Builder
public record RedisRequest(
        String className,
        String method,
        int line,
        String message,
        String severity,
        List<String> futureCalls
) {
}
