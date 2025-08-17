package com.cdbd.opensource.infrastructure;

import com.cdbd.opensource.domain.EventLog;
import com.cdbd.opensource.domain.EventLogRepository;
import com.cdbd.opensource.infrastructure.jpa.EventLogEntity;
import com.cdbd.opensource.infrastructure.jpa.FutureCallEntity;
import com.cdbd.opensource.infrastructure.jpa.JpaEventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventLogRepositoryImpl implements EventLogRepository {
    
    private final JpaEventLogRepository jpaRepository;
    
    @Override
    public void save(EventLog eventLog) {
        EventLogEntity entity = EventLogEntity.builder()
                .className(eventLog.getClassName())
                .method(eventLog.getMethod())
                .line(eventLog.getLine())
                .message(eventLog.getMessage())
                .severity(eventLog.getSeverity())
                .topic(eventLog.getTopic())
                .futureCalls(eventLog.getFutureCalls().stream()
                        .map(call -> FutureCallEntity.builder()
                                .callName(call)
                                .build())
                        .collect(Collectors.toList()))
                .build();

        jpaRepository.save(entity);
    }
}