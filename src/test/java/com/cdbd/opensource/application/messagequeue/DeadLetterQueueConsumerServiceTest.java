package com.cdbd.opensource.application.messagequeue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeadLetterQueueConsumerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private DeadLetterQueueConsumerService service;

    @BeforeEach
    void setUp() {
        service = new DeadLetterQueueConsumerService(kafkaTemplate);
        ReflectionTestUtils.setField(service, "dlqTopic", "test-dlq-topic");
    }

    @Test
    void listenMainTopic_정상메시지_처리성공() {
        // given
        String message = "normal message";

        // when
        service.listenMainTopic(message);

        // then
        // 정상 처리되어 DLQ로 전송되지 않음
    }

    @Test
    void listenMainTopic_에러메시지_DLQ로전송() {
        // given
        String message = "error message";

        // when
        service.listenMainTopic(message);

        // then
        verify(kafkaTemplate).send(eq("test-dlq-topic"), anyString());
    }
}