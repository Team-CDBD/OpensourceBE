package com.cdbd.opensource.application.messagequeue;

import com.cdbd.opensource.application.EventLogCommand;
import com.cdbd.opensource.application.EventLogFacade;
import com.cdbd.opensource.domain.eventlog.EventLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DynamicMessageListenerTest {

    @Mock
    private EventLogFacade eventLogFacade;

    private DynamicMessageListener listener;

    @BeforeEach
    void setUp() {
        listener = new DynamicMessageListener(eventLogFacade);
    }

    @Test
    void send_메시지를_받으면_EventLogFacade를_호출한다() {
        // given
        String topic = "test-topic";
        EventLog eventLog = new EventLog(null, "TestClass", "testMethod", 10, "test message", "INFO", List.of(), topic, "");

        // when
        listener.send(topic, eventLog);

        // then
        verify(eventLogFacade).save(any(EventLogCommand.class));
    }
}