package com.cdbd.opensource.infrastructure.redis;

public record RedisRequest(
        String key,
        String value
) {
}
