package com.cdbd.opensource.infrastructure.cache;

public interface CacheOperations {
    void set(CacheDTO dto);
    Object get(CacheKey key);
    Boolean delete(CacheKey key);
}
