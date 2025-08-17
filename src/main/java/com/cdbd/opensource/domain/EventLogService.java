package com.cdbd.opensource.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventLogService {
    private final EventLogRepository repository;

    public void save(EventLog log) {
        repository.save(log);
    }
}
