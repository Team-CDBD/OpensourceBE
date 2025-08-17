package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.EventLog;
import lombok.Builder;

@Builder
public record EventLogCommand (
        String topic,
        Object eventLog
) {
    public EventLog toEventLog() {
        EventLog log = (EventLog) eventLog;
        return new EventLog(
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
