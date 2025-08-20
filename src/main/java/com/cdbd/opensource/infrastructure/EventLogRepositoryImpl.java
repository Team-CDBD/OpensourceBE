package com.cdbd.opensource.infrastructure;

import com.cdbd.opensource.domain.eventlog.EventLog;
import com.cdbd.opensource.domain.eventlog.EventLogRepository;
import com.cdbd.opensource.infrastructure.jpa.EventLogEntity;
import com.cdbd.opensource.infrastructure.jpa.FutureCallEntity;
import com.cdbd.opensource.infrastructure.jpa.JpaEventLogRepository;
import com.cdbd.opensource.infrastructure.jpa.JpaFutureCallRepository;
import com.cdbd.opensource.presentation.eventlog.PageRequestDto;
import com.cdbd.opensource.presentation.eventlog.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventLogRepositoryImpl implements EventLogRepository {
    
    private final JpaEventLogRepository jpaEventLogRepository;
    private final JpaFutureCallRepository jpaFutureCallRepository;
    
    @Override
    public void save(EventLog eventLog) {
        EventLogEntity eventLogEntity = EventLogEntity.builder()
                .className(eventLog.getClassName())
                .method(eventLog.getMethod())
                .line(eventLog.getLine())
                .message(eventLog.getMessage())
                .severity(eventLog.getSeverity())
                .topic(eventLog.getTopic())
                .result(eventLog.getResult())
                .build();

        EventLogEntity savedEntity = jpaEventLogRepository.save(eventLogEntity);

        List<FutureCallEntity> futureCallEntities = eventLog.getFutureCalls().stream()
                .map(call -> FutureCallEntity.builder()
                        .callName(call)
                        .eventLog(savedEntity) // ⭐ savedEventLog 객체 참조
                        .build())
                .collect(Collectors.toList());

        jpaFutureCallRepository.saveAll(futureCallEntities);
    }

    @Override
    public PageResponseDto<EventLog> getEventLogs(PageRequestDto pageRequest) {
        Sort.Direction direction = "asc".equalsIgnoreCase(pageRequest.getDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), Sort.by(direction, pageRequest.getSortBy()));
        Page<EventLogEntity> logEntityPage = jpaEventLogRepository.findAll(pageable);

        List<EventLog> logList = logEntityPage.getContent().stream()
                .map(logEntity -> {
                    List<FutureCallEntity> futureCallEntities = jpaFutureCallRepository.findByEventLogId(logEntity.getId());

                    // FutureCallEntity의 callName만 추출하여 리스트로 변환
                    List<String> futureCallStrings = futureCallEntities.stream()
                            .map(FutureCallEntity::getCallName)
                            .collect(Collectors.toList());

                    // EventLogEntity와 조회한 futureCalls를 포함하여 DTO로 변환
                    return new EventLog(
                            logEntity.getId(),
                            logEntity.getClassName(),
                            logEntity.getMethod(),
                            logEntity.getLine(),
                            logEntity.getMessage(),
                            logEntity.getSeverity(),
                            futureCallStrings, // ⭐ 별도 조회한 futureCalls 목록 사용
                            logEntity.getTopic(),
                            logEntity.getResult()
                    );
                })
                .toList();

        return PageResponseDto.<EventLog>builder()
                .content(logList)
                .currentPage(logEntityPage.getNumber())
                .pageSize(logEntityPage.getSize())
                .totalElements(logEntityPage.getTotalElements())
                .totalPages(logEntityPage.getTotalPages())
                .build();
    }

    @Override
    public Optional<EventLog> findSameLog(EventLog log) {
        Optional<EventLogEntity> entity = jpaEventLogRepository.findSameLog(log);
        if (entity.isEmpty()) return  Optional.empty();
        EventLog newLog = new EventLog(
                null,
                log.getClassName(),
                log.getMethod(),
                log.getLine(),
                log.getMessage(),
                log.getSeverity(),
                log.getFutureCalls(),
                null,
                entity.get().getResult()
        );
        return Optional.of(newLog);
    }
}