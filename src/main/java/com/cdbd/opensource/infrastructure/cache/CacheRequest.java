package com.cdbd.opensource.infrastructure.cache;

import com.cdbd.opensource.domain.EventLog;
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
    public static CacheRequest fromEventLog(EventLog log) {
        return CacheRequest.builder()
                .className(log.getClassName())
                .method(log.getMethod())
                .futureCalls(log.getFutureCalls())
                .build();
    }
}
