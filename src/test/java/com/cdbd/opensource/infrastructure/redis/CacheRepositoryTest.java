package com.cdbd.opensource.infrastructure.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheRepositoryTest {
    @Mock
    private ValueOperations<String, Object> valueOps;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private CacheRepository cacheRepository;

    private RedisRequest testRequest;
    private RedisResponse testResponse;
    private String expectedKey;

    @BeforeEach
    void setUp() {
        testRequest = RedisRequest.builder()
                .className("com.example.TestClass")
                .method("testMethod")
                .line(42)
                .message("Test error message")
                .severity("ERROR")
                .futureCalls(List.of("method1()", "method2()"))
                .build();

        testResponse = RedisResponse.builder()
                .className("com.example.TestClass")
                .method("testMethod")
                .line(42)
                .message("Test error message")
                .severity("ERROR")
                .futureCalls(List.of("method1()", "method2()"))
                .ttl(Duration.ofMinutes(10))
                .build();

        expectedKey = "com.example.TestClass:testMethod:42:" + "Test error message".hashCode();
    }

    @Test
    void save_ShouldStoreRedisResponseWithCorrectKey() {
        // when
        cacheRepository.save(testRequest);

        // then
        verify(valueOps).set(eq(expectedKey), any(RedisResponse.class), eq(Duration.ofMinutes(10)));
    }

    @Test
    void find_WhenCacheExists_ShouldReturnRedisResponse() {
        // given
        when(valueOps.get(expectedKey)).thenReturn(testResponse);

        // when
        Optional<RedisResponse> result = cacheRepository.find(testRequest);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testResponse);
        verify(valueOps).get(expectedKey);
    }

    @Test
    void find_WhenCacheDoesNotExist_ShouldReturnEmpty() {
        // given
        when(valueOps.get(expectedKey)).thenReturn(null);

        // when
        Optional<RedisResponse> result = cacheRepository.find(testRequest);

        // then
        assertThat(result).isEmpty();
        verify(valueOps).get(expectedKey);
    }

    @Test
    void find_WhenCacheContainsWrongType_ShouldReturnEmpty() {
        // given
        when(valueOps.get(expectedKey)).thenReturn("wrong type");

        // when
        Optional<RedisResponse> result = cacheRepository.find(testRequest);

        // then
        assertThat(result).isEmpty();
        verify(valueOps).get(expectedKey);
    }

    @Test
    void delete_ShouldDeleteCacheWithCorrectKey() {
        // given
        when(valueOps.getOperations()).thenReturn(redisTemplate);
        when(redisTemplate.delete(expectedKey)).thenReturn(true);

        // when
        cacheRepository.delete(testRequest);

        // then
        verify(valueOps).getOperations();
        verify(redisTemplate).delete(expectedKey);
    }

    @Test
    void delete_WhenDataDoesNotExist_ShouldStillCallDelete() {
        // given
        when(valueOps.getOperations()).thenReturn(redisTemplate);
        when(redisTemplate.delete(expectedKey)).thenReturn(false);

        // when
        cacheRepository.delete(testRequest);

        // then
        verify(valueOps).getOperations();
        verify(redisTemplate).delete(expectedKey);
    }
}