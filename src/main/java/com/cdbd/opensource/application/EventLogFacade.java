package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.EventLog;
import com.cdbd.opensource.domain.EventLogService;
import com.cdbd.opensource.infrastructure.llm.LLMClient;
import com.cdbd.opensource.infrastructure.llm.LLMResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventLogFacade {
    private final LLMClient llmClient;
    private final EventLogService service;

    public void save(EventLogCommand command) {
        EventLog log = command.toEventLog();

        if (log.getSeverity().equals("ERROR")) {
            LLMResult result = llmClient.analyzeEventLog(log);
            log = new EventLog(
                    log.getClassName(),
                    log.getMethod(),
                    log.getLine(),
                    log.getMessage(),
                    log.getSeverity(),
                    log.getFutureCalls(),
                    log.getTopic(),
                    result.result()
            );
        }
        service.save(log);
    }
}
