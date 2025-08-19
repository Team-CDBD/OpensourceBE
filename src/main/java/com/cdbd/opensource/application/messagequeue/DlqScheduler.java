package com.cdbd.opensource.application.messagequeue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class DlqScheduler {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConcurrentLinkedQueue<FailedEventMessage> dlqQueue = new ConcurrentLinkedQueue<>();

    @Value("${kafka.dlq.topic}")
    private String dlqTopic;

    public DlqScheduler(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // DLQ 토픽에서 메시지를 컨슘하여 큐에 추가
    @KafkaListener(topics = "${kafka.dlq.topic}", groupId = "dlq-retry-group")
    public void listenDlq(String message) {
        try {
            dlqQueue.add(FailedEventMessage.fromJson(message));
        } catch (Exception e) {
            System.err.println("Error consuming DLQ message: " + e.getMessage());
        }
    }

    // 1분마다 재전송 로직 실행
    @Scheduled(fixedRate = 60000)
    public void retryFailedMessages() {
        System.out.println("Running scheduled retry for DLQ messages...");

        for (int i = 0; i < dlqQueue.size(); i++) {
            FailedEventMessage failedMessage = dlqQueue.poll();
            if (failedMessage == null) continue;

            if (failedMessage.getRetryCount() < 5) {
                System.out.println("Retrying message for topic: " + failedMessage.getOriginalTopic() + " (Retry " + (failedMessage.getRetryCount() + 1) + ")");

                kafkaTemplate.send(failedMessage.getOriginalTopic(), failedMessage.getMessage())
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                failedMessage.setRetryCount(failedMessage.getRetryCount() + 1);
                                dlqQueue.add(failedMessage); // 재전송 실패 시 다시 큐에 넣기
                            }
                        });
            } else {
                System.err.println("Max retry count reached for message on topic: " + failedMessage.getOriginalTopic());
                // 최종 실패 알림 로직 (예: Slack, Email 등)
            }
        }
    }
}