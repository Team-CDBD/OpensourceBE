package com.cdbd.opensource.domain;

import com.cdbd.opensource.infrastructure.redis.CacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventLogServiceTest {

    @Mock
    private EventLogRepository repository;

    @Mock
    private CacheRepository cacheRepository;

    private EventLogService service;

    @BeforeEach
    void setUp() {
        service = new EventLogService(repository, cacheRepository);
    }

    @Test
    void save_EventLog를_저장한다() {
        // given
        EventLog eventLog = new EventLog(null, "TestClass", "testMethod", 10, "test message", "INFO", List.of(), "topic", "");

        // when
        service.save(eventLog);

        // then
        verify(repository).save(eventLog);
    }
}