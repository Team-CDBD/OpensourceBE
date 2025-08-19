package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.EventLog;
import com.cdbd.opensource.infrastructure.cache.CacheRequest;
import com.cdbd.opensource.infrastructure.redis.CacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CacheEventListener {
    private final CacheRepository cacheRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleCacheSaveEvent(CacheSaveEvent event) {
        EventLog eventLog = event.getEventLog();
        cacheRepository.save(CacheRequest.fromEventLog(eventLog));
    }
}
