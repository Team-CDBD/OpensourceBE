package com.cdbd.opensource.application.messagequeue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterQueueConsumerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.dlq.topic}")
    private String dlqTopic;

    public DeadLetterQueueConsumerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${spring.kafka.main.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenMainTopic(String message) {
        System.out.println("Received message from main topic: " + message);

        try {
            // 메시지 처리 로직 (여기서 오류 발생을 가정)
            // 예: 데이터베이스 저장, 비즈니스 로직 처리 등
            if (message.contains("error")) {
                throw new IllegalArgumentException("Invalid message format");
            }

            System.out.println("Message processed successfully.");
        } catch (Exception e) {
            System.err.println("Failed to process message: " + e.getMessage());

            // 처리 실패 시 DLQ로 메시지 전송
            sendToDlq(message, e.getMessage());
        }
    }

    private void sendToDlq(String message, String failureReason) {
        try {
            FailedEventMessage failedMessage = FailedEventMessage.builder()
                    .originalTopic("kafka.main.topic")
                    .message(message)
                    .retryCount(0)
                    .failureReason(failureReason)
                    .failureTimestamp(System.currentTimeMillis())
                    .build();

            kafkaTemplate.send(dlqTopic, FailedEventMessage.toJson(failedMessage));
            System.out.println("Message sent to DLQ topic: " + dlqTopic);
        } catch (Exception e) {
            System.err.println("Failed to send message to DLQ: " + e.getMessage());
        }
    }
}
