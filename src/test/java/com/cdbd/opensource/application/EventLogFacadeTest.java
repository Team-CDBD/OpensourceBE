package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.eventlog.EventLog;
import com.cdbd.opensource.domain.eventlog.EventLogService;
import com.cdbd.opensource.infrastructure.llm.LLMClient;
import com.cdbd.opensource.infrastructure.llm.LLMResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventLogFacadeTest {

    @Mock
    private LLMClient llmClient;
    
    @Mock
    private EventLogService eventLogService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private EventLogFacade facade;

    @BeforeEach
    void setUp() {
        facade = new EventLogFacade(llmClient, eventLogService, eventPublisher);
    }

    @Test
    void save_ERROR로그가_아니면_LLM분석없이_저장() {
        // given
        EventLog eventLog = new EventLog(null, "TestClass", "testMethod", 10, "test message", "INFO", List.of(), "topic", "");
        EventLogCommand command = EventLogCommand.builder().topic("topic").eventLog(eventLog).build();

        // when
        facade.save(command);

        // then
        verify(llmClient, never()).analyzeEventLog(any());
        verify(eventLogService).save(any(EventLog.class));
    }

    @Test
    void save_ERROR로그면_LLM분석후_저장() {
        // given
        EventLog eventLog = new EventLog(null, "TestClass", "testMethod", 10, "error message", "ERROR", List.of(), "topic", "");
        EventLogCommand command = EventLogCommand.builder().topic("topic").eventLog(eventLog).build();
        LLMResult llmResult = new LLMResult("LLM analysis result");
        when(llmClient.analyzeEventLog(any())).thenReturn(llmResult);

        // when
        facade.save(command);

        // then
        verify(llmClient).analyzeEventLog(any());
        verify(eventLogService).save(any(EventLog.class));
    }
}