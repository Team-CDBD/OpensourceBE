package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.EventLog;
import com.cdbd.opensource.domain.EventLogRepository;
import com.cdbd.opensource.infrastructure.llm.LLMClient;
import com.cdbd.opensource.infrastructure.llm.LLMResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class EventLogIntegrationTest {

    @Autowired
    private EventLogFacade eventLogFacade;

    @Mock
    private LLMClient llmClient;
    
    @Mock
    private EventLogRepository eventLogRepository;

    @Test
    void EventLog_전체플로우_통합테스트() {
        // given
        EventLog eventLog = new EventLog(null,"TestClass", "testMethod", 10, "error message", "ERROR", List.of("call1"), "original-topic", "");
        EventLogCommand command = EventLogCommand.builder()
                .topic("test-topic")
                .eventLog(eventLog)
                .build();
        
        LLMResult llmResult = new LLMResult("LLM analysis result");
        when(llmClient.analyzeEventLog(any())).thenReturn(llmResult);

        // when
        eventLogFacade.save(command);

        // then
        verify(llmClient).analyzeEventLog(any());
        verify(eventLogRepository).save(any(EventLog.class));
    }

    @Test
    void INFO로그_LLM분석없이_저장() {
        // given
        EventLog eventLog = new EventLog(null, "TestClass", "testMethod", 10, "info message", "INFO", List.of(), "original-topic", "");
        EventLogCommand command = EventLogCommand.builder()
                .topic("test-topic")
                .eventLog(eventLog)
                .build();

        // when
        eventLogFacade.save(command);

        // then
        verify(llmClient, never()).analyzeEventLog(any());
        verify(eventLogRepository).save(any(EventLog.class));
    }
}