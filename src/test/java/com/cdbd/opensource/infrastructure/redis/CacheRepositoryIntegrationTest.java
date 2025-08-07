package com.cdbd.opensource.infrastructure.redis;

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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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

    private RedisRequest testRequest;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        
        testRequest = RedisRequest.builder()
                .className("com.example.TestClass")
                .method("testMethod")
                .line(42)
                .message("Test error message")
                .severity("ERROR")
                .futureCalls(List.of("method1()", "method2()"))
                .build();
    }

    @Test
    void saveAndFind_ShouldWorkCorrectly() {
        // when
        cacheRepository.save(testRequest);
        Optional<RedisResponse> result = cacheRepository.find(testRequest);

        // then
        assertThat(result).isPresent();
        RedisResponse response = result.get();
        assertThat(response.className()).isEqualTo("com.example.TestClass");
        assertThat(response.method()).isEqualTo("testMethod");
        assertThat(response.line()).isEqualTo(42);
        assertThat(response.message()).isEqualTo("Test error message");
        assertThat(response.severity()).isEqualTo("ERROR");
        assertThat(response.futureCalls()).containsExactly("method1()", "method2()");
    }

    @Test
    void find_WhenDataDoesNotExist_ShouldReturnEmpty() {
        // when
        Optional<RedisResponse> result = cacheRepository.find(testRequest);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void delete_ShouldRemoveDataFromCache() {
        // given
        cacheRepository.save(testRequest);
        assertThat(cacheRepository.find(testRequest)).isPresent();

        // when
        cacheRepository.delete(testRequest);

        // then
        assertThat(cacheRepository.find(testRequest)).isEmpty();
    }

    @Test
    void delete_WhenDataDoesNotExist_ShouldNotThrowException() {
        // when & then
        cacheRepository.delete(testRequest);
        assertThat(cacheRepository.find(testRequest)).isEmpty();
    }
}