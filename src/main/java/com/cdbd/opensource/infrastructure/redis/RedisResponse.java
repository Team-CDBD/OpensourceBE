package com.cdbd.opensource.infrastructure.redis;

public record RedisResponse(
        String key,
        Object value,
        Duration ttl
) {
}
