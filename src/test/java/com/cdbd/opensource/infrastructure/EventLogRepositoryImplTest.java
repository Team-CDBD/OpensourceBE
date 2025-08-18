package com.cdbd.opensource.infrastructure;

import com.cdbd.opensource.domain.EventLog;
import com.cdbd.opensource.infrastructure.jpa.EventLogEntity;
import com.cdbd.opensource.infrastructure.jpa.EventLogJpaRepository;
import com.cdbd.opensource.infrastructure.jpa.FutureCallEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventLogRepositoryImplTest {

    @Mock
    private EventLogJpaRepository jpaRepository;

    private EventLogRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new EventLogRepositoryImpl(jpaRepository);
    }

    @Test
    void save_EventLog를_엔티티로_변환하여_저장() {
        // given
        EventLog eventLog = new EventLog(
                "TestClass",
                "testMethod",
                10,
                "test message",
                "ERROR",
                List.of("call1", "call2"),
                "test-topic",
                "llm result"
        );

        EventLogEntity savedEntity = EventLogEntity.builder()
                .id(1L)
                .className("TestClass")
                .method("testMethod")
                .line(10)
                .message("test message")
                .severity("ERROR")
                .topic("test-topic")
                .build();

        when(jpaRepository.save(any(EventLogEntity.class))).thenReturn(savedEntity);

        // when
        repository.save(eventLog);

        // then
        ArgumentCaptor<EventLogEntity> captor = ArgumentCaptor.forClass(EventLogEntity.class);
        verify(jpaRepository).save(captor.capture());
        
        EventLogEntity capturedEntity = captor.getValue();
        assertThat(capturedEntity.getClassName()).isEqualTo("TestClass");
        assertThat(capturedEntity.getMethod()).isEqualTo("testMethod");
        assertThat(capturedEntity.getLine()).isEqualTo(10);
        assertThat(capturedEntity.getMessage()).isEqualTo("test message");
        assertThat(capturedEntity.getSeverity()).isEqualTo("ERROR");
        assertThat(capturedEntity.getTopic()).isEqualTo("test-topic");
        assertThat(capturedEntity.getFutureCalls()).hasSize(2);
    }

    @Test
    void save_FutureCall_양방향관계_설정() {
        // given
        EventLog eventLog = new EventLog(
                "TestClass",
                "testMethod",
                10,
                "test message",
                "INFO",
                List.of("call1"),
                "test-topic",
                ""
        );

        // when
        repository.save(eventLog);

        // then
        ArgumentCaptor<EventLogEntity> captor = ArgumentCaptor.forClass(EventLogEntity.class);
        verify(jpaRepository).save(captor.capture());
        
        EventLogEntity capturedEntity = captor.getValue();
        FutureCallEntity futureCall = capturedEntity.getFutureCalls().get(0);
        assertThat(futureCall.getCallName()).isEqualTo("call1");
        assertThat(futureCall.getEventLog()).isEqualTo(capturedEntity);
    }
}