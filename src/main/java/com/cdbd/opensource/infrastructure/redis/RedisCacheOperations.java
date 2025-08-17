package com.cdbd.opensource.infrastructure.redis;

import com.cdbd.opensource.infrastructure.cache.CacheDTO;
import com.cdbd.opensource.infrastructure.cache.CacheKey;
import com.cdbd.opensource.infrastructure.cache.CacheOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCacheOperations implements CacheOperations {
    private final ValueOperations<String, Object> valueOperations;

    @Override
    public void set(CacheDTO dto) {
        valueOperations.set(dto.key().key().toString(), dto.data(), dto.ttl());
    }

    @Override
    public Object get(CacheKey key) {
        return valueOperations.get(key.key());
    }

    @Override
    public Boolean delete(CacheKey key) {
        return valueOperations.getOperations().delete(key.key());
    }
}
