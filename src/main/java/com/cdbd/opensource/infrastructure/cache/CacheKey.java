package com.cdbd.opensource.infrastructure.cache;

import java.util.Arrays;
import java.util.List;

public record CacheKey(
        String key
) {
    public static CacheKey from (CacheRequest request) {
        return new CacheKey(String.format("%s:%s:%d:%s",
                request.className(),
                request.method(),
                request.line(),
                request.message().hashCode()));
    }

    public List<String> parseKey() {
        return Arrays.asList(key.split(":"));
    }
}
