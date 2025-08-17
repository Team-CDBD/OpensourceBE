package com.cdbd.opensource.infrastructure;

import com.cdbd.opensource.domain.EventLog;
import com.cdbd.opensource.domain.EventLogRepository;
import com.cdbd.opensource.infrastructure.jpa.EventLogEntity;
import com.cdbd.opensource.infrastructure.jpa.FutureCallEntity;
import com.cdbd.opensource.infrastructure.jpa.JpaEventLogRepository;
import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Override
    public PageResponseDto<EventLog> getEventLogs(PageRequestDto pageRequest) {
        Sort.Direction direction = "asc".equalsIgnoreCase(pageRequest.getDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), Sort.by(direction, pageRequest.getSortBy()));
        Page<EventLogEntity> logEntityPage = jpaRepository.findAll(pageable);

        List<EventLog> logList = logEntityPage.getContent().stream().map(EventLogEntity::toEventLog).toList();

        return new PageResponseDto<EventLog>(
                logList,
                logEntityPage.getNumber(),
                logEntityPage.getSize(),
                logEntityPage.getTotalElements(),
                logEntityPage.getTotalPages()
        );
    }
}