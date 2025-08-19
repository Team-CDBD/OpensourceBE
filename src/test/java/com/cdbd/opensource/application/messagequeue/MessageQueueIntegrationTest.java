package com.cdbd.opensource.application.messagequeue;

import com.cdbd.opensource.application.EventLogFacade;
import com.cdbd.opensource.domain.EventLog;
import com.cdbd.opensource.infrastructure.TopicRepositoryImpl;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {
    "kafka.dlq.topic=test-dlq-topic",
    "kafka.main.topic=test-main-topic",
    "spring.kafka.consumer.group-id=test-group"
})
class MessageQueueIntegrationTest {

    @Mock
    private TopicRepositoryImpl dynamicSubscriptionRepository;
    
    @Mock
    private EventLogFacade eventLogFacade;
    
    @Mock
    private KafkaConsumer<String, Object> kafkaConsumer;

    @Test
    void 메시지큐_전체플로우_통합테스트() {
        // given
        when(dynamicSubscriptionRepository.findAllTopics()).thenReturn(Set.of("test-topic"));
        
        DynamicMessageListener listener = new DynamicMessageListener(eventLogFacade);
        EventLog eventLog = new EventLog(null, "TestClass", "testMethod", 10, "test message", "INFO", List.of(), "test-topic", "");

        // when
        listener.send("test-topic", eventLog);

        // then
        verify(eventLogFacade).save(any());
    }
}