package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.EventLog;
import com.cdbd.opensource.domain.EventLogRepository;
import com.cdbd.opensource.infrastructure.llm.LLMClient;
import com.cdbd.opensource.infrastructure.llm.LLMResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventLogService {
    private final LLMClient llmClient;
    private final EventLogRepository repository;

    public void save(EventLogCommand command) {
        EventLog log = command.toEventLog();

        if (log.getSeverity().equals("ERROR")) {
            LLMResult result = llmClient.analyzeEventLog(log);

        } else {
            repository.save(log);
        }
    }
}
