package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.eventlog.EventLog;
import lombok.Builder;

@Builder
public record EventLogCommand (
        String topic,
        Object eventLog
) {
    public EventLog toEventLog() {
        EventLog log = (EventLog) eventLog;
        return new EventLog(
                log.getId(),
                log.getClassName(),
                log.getMethod(),
                log.getLine(),
                log.getMessage(),
                log.getSeverity(),
                log.getFutureCalls(),
                topic,
                ""
        );
    }
}
