package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.EventLog;
import com.cdbd.opensource.infrastructure.cache.CacheRequest;
import com.cdbd.opensource.infrastructure.redis.CacheRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
@Transactional
class CacheIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private CacheRepository cacheRepository;

    @Test
    void CacheSaveEvent_발행시_CacheEventListener가_처리() {
        // given
        EventLog eventLog = new EventLog(
                "TestClass",
                "testMethod",
                10,
                "integration test message",
                "ERROR",
                List.of("call1", "call2"),
                "test-topic",
                "llm integration result"
        );
        CacheSaveEvent event = new CacheSaveEvent(eventLog);

        // when
        eventPublisher.publishEvent(event);

        // then
        // @TransactionalEventListener는 트랜잭션 커밋 후 실행되므로 timeout 사용
        verify(cacheRepository, timeout(1000)).save(any(CacheRequest.class));
    }

    @Test
    void 여러_CacheSaveEvent_연속_발행_처리() {
        // given
        EventLog eventLog1 = new EventLog("Class1", "method1", 1, "message1", "ERROR", List.of("call1"), "topic1", "result1");
        EventLog eventLog2 = new EventLog("Class2", "method2", 2, "message2", "WARN", List.of("call2"), "topic2", "result2");
        
        CacheSaveEvent event1 = new CacheSaveEvent(eventLog1);
        CacheSaveEvent event2 = new CacheSaveEvent(eventLog2);

        // when
        eventPublisher.publishEvent(event1);
        eventPublisher.publishEvent(event2);

        // then
        verify(cacheRepository, timeout(1000).times(2)).save(any(CacheRequest.class));
    }
}