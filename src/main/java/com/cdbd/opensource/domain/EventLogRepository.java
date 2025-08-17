package com.cdbd.opensource.domain;

import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;

public interface EventLogRepository {
    void save(EventLog log);

    PageResponseDto<EventLog> getEventLogs(PageRequestDto pageRequest);
}
