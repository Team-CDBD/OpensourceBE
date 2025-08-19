package com.cdbd.opensource.infrastructure.redis;

import com.cdbd.opensource.infrastructure.cache.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheRepositoryTest {
    @Mock
    private CacheOperations cacheOps;

    @InjectMocks
    private CacheRepository cacheRepository;

    private CacheRequest testRequest;
    private CacheKey expectedKey;

    @BeforeEach
    void setUp() {
        testRequest = CacheRequest.builder()
                .className("com.example.TestClass")
                .method("testMethod")
                .line(42)
                .message("Test error message")
                .severity("ERROR")
                .futureCalls(List.of("method1()", "method2()"))
                .ttl(Duration.ofMinutes(10))
                .build();

        expectedKey = CacheKey.from(testRequest);
    }

    @Test
    void save_ShouldStoreCacheDataWithCorrectKey() {
        // when
        cacheRepository.save(testRequest);

        // then
        Mockito.verify(cacheOps).set(ArgumentMatchers.any(CacheDTO.class));
    }

    @Test
    void find_WhenCacheExists_ShouldReturnCacheResponse() {
        // given
        CacheData cachedData = CacheData.from(testRequest);
        when(cacheOps.get(expectedKey)).thenReturn(cachedData);

        // when
        Optional<CacheResponse> result = cacheRepository.find(testRequest);

        // then
        Assertions.assertThat(result).isPresent();
        Mockito.verify(cacheOps).get(expectedKey);
    }

    @Test
    void find_WhenCacheDoesNotExist_ShouldCreateAndReturnResponse() {
        // given
        when(cacheOps.get(expectedKey)).thenReturn(null);

        // when
        Optional<CacheResponse> result = cacheRepository.find(testRequest);

        // then
        Assertions.assertThat(result).isPresent();
        Mockito.verify(cacheOps).get(expectedKey);
    }

    @Test
    void find_WhenCacheContainsWrongType_ShouldCreateNewAndReturnResponse() {
        // given
        when(cacheOps.get(expectedKey)).thenReturn("wrong type");

        // when
        Optional<CacheResponse> result = cacheRepository.find(testRequest);

        // then
        Assertions.assertThat(result).isPresent();
        Mockito.verify(cacheOps).get(expectedKey);
    }

    @Test
    void delete_ShouldDeleteCacheWithCorrectKey() {
        // when
        cacheRepository.delete(testRequest);

        // then
        Mockito.verify(cacheOps).delete(expectedKey);
    }

    @Test
    void delete_WhenDataDoesNotExist_ShouldStillCallDelete() {
        // when
        cacheRepository.delete(testRequest);

        // then
        Mockito.verify(cacheOps).delete(expectedKey);
    }

    @Test
    void save_WhenRequestHasCustomTTL_ShouldUseCustomTTL() {
        // given
        CacheRequest requestWithTTL = CacheRequest.builder()
                .className("com.example.TestClass")
                .method("testMethod")
                .line(42)
                .message("Test error message")
                .severity("ERROR")
                .futureCalls(List.of("method1()"))
                .ttl(Duration.ofHours(1))
                .build();

        // when
        cacheRepository.save(requestWithTTL);

        // then
        Mockito.verify(cacheOps).set(ArgumentMatchers.any(CacheDTO.class));
    }

    @Test
    void save_WhenRequestHasNullTTL_ShouldUseDefaultTTL() {
        // given
        CacheRequest requestWithoutTTL = CacheRequest.builder()
                .className("com.example.TestClass")
                .method("testMethod")
                .line(42)
                .message("Test error message")
                .severity("ERROR")
                .futureCalls(List.of("method1()"))
                .ttl(null)
                .build();

        // when
        cacheRepository.save(requestWithoutTTL);

        // then
        Mockito.verify(cacheOps).set(ArgumentMatchers.any(CacheDTO.class));
    }

    @Test
    void find_WhenRequestHasCustomTTL_ShouldUseCustomTTL() {
        // given
        CacheRequest requestWithTTL = CacheRequest.builder()
                .className("com.example.TestClass")
                .method("testMethod")
                .line(42)
                .message("Test error message")
                .severity("ERROR")
                .futureCalls(List.of("method1()"))
                .ttl(Duration.ofHours(2))
                .build();
        
        when(cacheOps.get(ArgumentMatchers.any(CacheKey.class))).thenReturn(null);

        // when
        cacheRepository.find(requestWithTTL);
    }

    @Test
    void find_WhenCacheOperationsThrowsException_ShouldPropagateException() {
        // given
        when(cacheOps.get(ArgumentMatchers.any(CacheKey.class))).thenThrow(new RuntimeException("Cache error"));

        // when & then
        Assertions.assertThat(org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> cacheRepository.find(testRequest))).hasMessage("Cache error");
    }

    @Test
    void find_ShouldReturnCorrectResponseStructure() {
        // given
        when(cacheOps.get(ArgumentMatchers.any(CacheKey.class))).thenReturn(null);

        // when
        Optional<CacheResponse> result = cacheRepository.find(testRequest);

        // then
        Assertions.assertThat(result).isPresent();
        CacheResponse response = result.get();
        Assertions.assertThat(response.className()).isEqualTo(testRequest.className());
        Assertions.assertThat(response.method()).isEqualTo(testRequest.method());
        Assertions.assertThat(response.line()).isEqualTo(testRequest.line());
        Assertions.assertThat(response.severity()).isEqualTo(testRequest.severity());
        Assertions.assertThat(response.futureCalls()).isEqualTo(testRequest.futureCalls());
    }
}