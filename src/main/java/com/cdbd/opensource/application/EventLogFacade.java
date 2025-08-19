package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.EventLog;
import com.cdbd.opensource.domain.EventLogService;
import com.cdbd.opensource.infrastructure.llm.LLMClient;
import com.cdbd.opensource.infrastructure.llm.LLMResult;
import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventLogFacade {
    private final LLMClient llmClient;
    private final EventLogService service;
    private final ApplicationEventPublisher eventPublisher;

    public void save(EventLogCommand command) {
        EventLog log = command.toEventLog();

        if (log.getSeverity().equals("ERROR")) {
            Optional<EventLog> sameError = service.getSameError(log);

            if (sameError.isEmpty()) {
                LLMResult result = llmClient.analyzeEventLog(log);
                log = new EventLog(
                        log.getId(),
                        log.getClassName(),
                        log.getMethod(),
                        log.getLine(),
                        log.getMessage(),
                        log.getSeverity(),
                        log.getFutureCalls(),
                        log.getTopic(),
                        result.result()
                );
            } else {
                log = sameError.get();
            }
        }

        service.save(log);

        if (log.getSeverity().equals("ERROR")) {
            eventPublisher.publishEvent(new CacheSaveEvent(log));
        }
    }

    public PageResponseDto<EventLog> getEventLogs(PageRequestDto pageRequest) {
        return service.getEventLogs(pageRequest);
    }
}
