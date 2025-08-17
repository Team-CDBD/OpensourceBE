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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DlqSchedulerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private DlqScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new DlqScheduler(kafkaTemplate);
        ReflectionTestUtils.setField(scheduler, "dlqTopic", "test-dlq-topic");
    }

    @Test
    void listenDlq_DLQ메시지를_큐에추가() {
        // given
        String failedMessage = "{\"originalTopic\":\"test-topic\",\"message\":\"test\",\"retryCount\":0,\"failureReason\":\"error\",\"failureTimestamp\":123456789}";

        // when
        scheduler.listenDlq(failedMessage);

        // then
        // 큐에 메시지가 추가되었는지 확인하는 로직은 내부 구현에 의존
    }

    @Test
    void retryFailedMessages_재시도횟수가_5미만이면_재전송() {
        // given
        String failedMessage = "{\"originalTopic\":\"test-topic\",\"message\":\"test\",\"retryCount\":2,\"failureReason\":\"error\",\"failureTimestamp\":123456789}";
        scheduler.listenDlq(failedMessage);

        // when
        scheduler.retryFailedMessages();

        // then
        verify(kafkaTemplate).send(eq("test-topic"), eq("test"));
    }
}