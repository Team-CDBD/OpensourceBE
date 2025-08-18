package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.EventLog;
import com.cdbd.opensource.infrastructure.cache.CacheRequest;
import com.cdbd.opensource.infrastructure.redis.CacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CacheEventListenerTest {

    @Mock
    private CacheRepository cacheRepository;

    private CacheEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new CacheEventListener(cacheRepository);
    }

    @Test
    void handleCacheSaveEvent_이벤트로그를_캐시에_저장() {
        // given
        EventLog eventLog = new EventLog(
                1L,
                "TestClass",
                "testMethod",
                10,
                "test message",
                "ERROR",
                List.of("call1", "call2"),
                "test-topic",
                "llm result"
        );
        CacheSaveEvent event = new CacheSaveEvent(eventLog);

        // when
        listener.handleCacheSaveEvent(event);

        // then
        ArgumentCaptor<CacheRequest> captor = ArgumentCaptor.forClass(CacheRequest.class);
        verify(cacheRepository).save(captor.capture());
        
        CacheRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.message()).isEqualTo("test message");
        assertThat(capturedRequest.severity()).isEqualTo("ERROR");
        assertThat(capturedRequest.futureCalls()).containsExactly("call1", "call2");
        assertThat(capturedRequest.result()).isEqualTo("llm result");
    }

    @Test
    void handleCacheSaveEvent_빈_futureCalls_처리() {
        // given
        EventLog eventLog = new EventLog(
                2L,
                "TestClass",
                "testMethod",
                10,
                "test message",
                "INFO",
                List.of(),
                "test-topic",
                ""
        );
        CacheSaveEvent event = new CacheSaveEvent(eventLog);

        // when
        listener.handleCacheSaveEvent(event);

        // then
        ArgumentCaptor<CacheRequest> captor = ArgumentCaptor.forClass(CacheRequest.class);
        verify(cacheRepository).save(captor.capture());
        
        CacheRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.futureCalls()).isEmpty();
        assertThat(capturedRequest.result()).isEmpty();
    }
}