package com.cdbd.opensource.infrastructure.redis;

import com.cdbd.opensource.infrastructure.cache.CacheRequest;
import com.cdbd.opensource.infrastructure.cache.CacheResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Testcontainers
class CacheRepositoryIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private CacheRequest testRequest;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        
        testRequest = CacheRequest.builder()
                .className("com.example.TestClass")
                .method("testMethod")
                .line(42)
                .message("Test error message")
                .severity("ERROR")
                .futureCalls(List.of("method1()", "method2()"))
                .ttl(Duration.ofMinutes(10))
                .build();
    }

    @Test
    void saveAndFind_ShouldWorkCorrectly() {
        // when
        cacheRepository.save(testRequest);
        Optional<CacheResponse> result = cacheRepository.find(testRequest);

        // then
        Assertions.assertThat(result).isPresent();
        CacheResponse response = result.get();
        Assertions.assertThat(response.className()).isEqualTo("com.example.TestClass");
        Assertions.assertThat(response.method()).isEqualTo("testMethod");
        Assertions.assertThat(response.line()).isEqualTo(42);
        Assertions.assertThat(response.message()).isEqualTo("Test error message");
        Assertions.assertThat(response.severity()).isEqualTo("ERROR");
        Assertions.assertThat(response.futureCalls()).containsExactly("method1()", "method2()");
    }

    @Test
    void find_WhenDataDoesNotExist_ShouldCreateAndReturnResponse() {
        // when
        Optional<CacheResponse> result = cacheRepository.find(testRequest);

        // then
        Assertions.assertThat(result).isPresent();
        CacheResponse response = result.get();
        Assertions.assertThat(response.className()).isEqualTo("com.example.TestClass");
        Assertions.assertThat(response.method()).isEqualTo("testMethod");
        Assertions.assertThat(response.line()).isEqualTo(42);
        Assertions.assertThat(response.severity()).isEqualTo("ERROR");
    }

    @Test
    void delete_ShouldRemoveDataFromCache() {
        // given
        cacheRepository.save(testRequest);
        Assertions.assertThat(cacheRepository.find(testRequest)).isPresent();

        // when
        cacheRepository.delete(testRequest);
        
        // then - find will create new data since cache was deleted
        Optional<CacheResponse> result = cacheRepository.find(testRequest);
        Assertions.assertThat(result).isPresent();
    }

    @Test
    void delete_WhenDataDoesNotExist_ShouldNotThrowException() {
        // when & then
        cacheRepository.delete(testRequest);
        Assertions.assertThat(cacheRepository.find(testRequest)).isPresent();
    }
}