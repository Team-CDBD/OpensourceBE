package com.cdbd.opensource.infrastructure.cache;

import java.time.Duration;
import java.util.List;

public record CacheDTO(
        CacheKey key,
        CacheData data,
        Duration ttl
) {
    public CacheResponse toCacheResponse () {
        List<String> parsedKey = key.parseKey();
        return CacheResponse.builder()
                .className(parsedKey.get(0))
                .method(parsedKey.get(1))
                .line(Integer.parseInt(parsedKey.get(2)))
                .message(data().message())
                .severity(data().severity())
                .futureCalls(data().futureCalls())
                .build();
    }
}
