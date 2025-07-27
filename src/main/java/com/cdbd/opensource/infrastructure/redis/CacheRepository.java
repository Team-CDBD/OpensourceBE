package com.cdbd.opensource.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CacheRepository {
    private final RedisClient redisClient;

    public void save(RedisRequest request) {
        redisClient.save(request);
    }
}
