package com.cdbd.opensource.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static sun.net.www.protocol.http.AuthenticatorKeys.getKey;

@Repository
@RequiredArgsConstructor
public class CacheRepository {
    private static final Duration TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;

    public void save(RedisRequest request) {
        String key = request.key();
        valueOps.set(key, request, TTL);
    }

    public Optional<RedisResponse> find(RedisRequest request) {
        String redisKey = request.key();
        Object cached = valueOps.get(redisKey);

        if (cached instanceof RedisResponse cachedResponse) {
            if (cachedResponseMatches(request.value().error(), cachedResponse)) {
                return Optional.of(cachedResponse);
            }
        }
        return Optional.empty();
    }

    private boolean cachedResponseMatches(Throwable currentError, RedisResponse cachedResponse) {
        if (cachedResponse == null || currentError == null) return false;

        // cachedResponse.data에는 이전 예외 정보가 담겨 있다고 가정
        if (!(cachedResponse.value() instanceof Throwable cachedError)) return false;

        return isSameError(currentError, cachedError);
    }

    private boolean isSameError(Throwable current, Throwable cached) {
        // 1. 에러 클래스명이 같은가
        if (!current.getClass().getName().equals(cached.getClass().getName())) return false;

        // 2. 최초 발생한 위치가 같은가
        StackTraceElement[] currentStack = current.getStackTrace();
        StackTraceElement[] cachedStack = cached.getStackTrace();

        if (currentStack.length == 0 || cachedStack.length == 0) return false;

        StackTraceElement currentTop = currentStack[0];
        StackTraceElement cachedTop = cachedStack[0];

        if (!Objects.equals(currentTop.getClassName(), cachedTop.getClassName())) return false;
        if (!Objects.equals(currentTop.getMethodName(), cachedTop.getMethodName())) return false;

        // 3. Stacktrace 해시 일부가 같은가 (앞 5개 요소 문자열 비교)
        String currentTraceSummary = getTraceSummary(currentStack);
        String cachedTraceSummary = getTraceSummary(cachedStack);

        return currentTraceSummary.equals(cachedTraceSummary);
    }

    private String getTraceSummary(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .limit(5)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("|"));
    }
}
