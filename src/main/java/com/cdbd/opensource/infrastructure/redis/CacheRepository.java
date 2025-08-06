package com.cdbd.opensource.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CacheRepository {
    private static final Duration TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;

    public void save(RedisRequest request) {
        String key = generateKey(request);
        RedisResponse response = RedisResponse.builder()
                .className(request.className())
                .method(request.method())
                .line(request.line())
                .message(request.message())
                .severity(request.severity())
                .futureCalls(request.futureCalls())
                .ttl(TTL)
                .build();
        valueOps.set(key, response, TTL);
    }

    public Optional<RedisResponse> find(RedisRequest request) {
        String key = generateKey(request);
        Object cached = valueOps.get(key);

        if (cached instanceof RedisResponse cachedResponse) {
            return Optional.of(cachedResponse);
        }
        return Optional.empty();
    }

    private String generateKey(RedisRequest request) {
        return String.format("%s:%s:%d:%s", 
                request.className(), 
                request.method(), 
                request.line(), 
                request.message().hashCode());
    }
}
