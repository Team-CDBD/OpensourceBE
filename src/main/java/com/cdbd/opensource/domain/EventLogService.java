package com.cdbd.opensource.domain;

import com.cdbd.opensource.infrastructure.cache.CacheRequest;
import com.cdbd.opensource.infrastructure.cache.CacheResponse;
import com.cdbd.opensource.infrastructure.redis.CacheRepository;
import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventLogService {
    private final EventLogRepository repository;
    private final CacheRepository cacheRepository;

    public void save(EventLog log) {
        repository.save(log);
    }

    public PageResponseDto<EventLog> getEventLogs(PageRequestDto pageRequest) {
        return repository.getEventLogs(pageRequest);
    }

    public Optional<EventLog> getSameError(EventLog log) {
        Optional<CacheResponse> cacheResponse = cacheRepository.find(CacheRequest.fromEventLog(log));
        if (cacheResponse.isPresent()) {
            EventLog newLog = new EventLog(
                    null,
                    log.getClassName(),
                    log.getMethod(),
                    log.getLine(),
                    log.getMessage(),
                    log.getSeverity(),
                    log.getFutureCalls(),
                    null,
                    cacheResponse.get().result()
            );
            return Optional.of(newLog);
        }

        return repository.findSameLog(log);

    }
}
