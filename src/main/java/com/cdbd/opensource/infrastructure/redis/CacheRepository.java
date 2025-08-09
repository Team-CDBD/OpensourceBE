package com.cdbd.opensource.infrastructure.redis;

import com.cdbd.opensource.infrastructure.cache.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CacheRepository {
    private static final Duration TTL = Duration.ofDays(30);

    private final CacheOperations cacheOps;

    public void save(CacheRequest request) {
        CacheKey key = CacheKey.from(request);
        CacheData data = CacheData.from(request);
        Duration ttl = Optional.ofNullable(request.ttl()).orElse(TTL);
        CacheDTO dto = new CacheDTO(key, data, ttl);
        cacheOps.set(dto);
    }

    public Optional<CacheResponse> find(CacheRequest request) {
        CacheKey key = CacheKey.from(request);
        Duration ttl = Optional.ofNullable(request.ttl()).orElse(TTL);

        CacheData data = Optional.ofNullable(cacheOps.get(key))
                .filter(CacheData.class::isInstance)
                .map(CacheData.class::cast)
                .orElseGet(() -> CacheData.from(request));

        CacheDTO dto = new CacheDTO(key, data, ttl);
        cacheOps.set(dto);
        return Optional.of(dto.toCacheResponse());
    }

    public void delete(CacheRequest request) {
        CacheKey key = CacheKey.from(request);

        cacheOps.delete(key);
    }
}
