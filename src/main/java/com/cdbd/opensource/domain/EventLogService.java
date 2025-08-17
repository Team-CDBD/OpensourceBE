package com.cdbd.opensource.domain;

import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventLogService {
    private final EventLogRepository repository;

    public void save(EventLog log) {
        repository.save(log);
    }

    public PageResponseDto<EventLog> getEventLogs(PageRequestDto pageRequest) {
        return repository.getEventLogs(pageRequest);
    }
}
