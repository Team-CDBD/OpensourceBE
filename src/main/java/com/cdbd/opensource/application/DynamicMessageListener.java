package com.cdbd.opensource.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DynamicMessageListener {
    private final EventLogService eventLogService;

    @Async
    public void send(String topic, Object eventLog) {
        EventLogCommand command = EventLogCommand.builder().topic(topic).eventLog(eventLog).build();
        eventLogService.save(command);
    }
}