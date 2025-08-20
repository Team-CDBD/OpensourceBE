package com.cdbd.opensource.domain.eventlog;

import com.cdbd.opensource.presentation.eventlog.PageRequestDto;
import com.cdbd.opensource.presentation.eventlog.PageResponseDto;

import java.util.Optional;

public interface EventLogRepository {
    void save(EventLog log);

    PageResponseDto<EventLog> getEventLogs(PageRequestDto pageRequest);

    Optional<EventLog> findSameLog(EventLog log);
}
