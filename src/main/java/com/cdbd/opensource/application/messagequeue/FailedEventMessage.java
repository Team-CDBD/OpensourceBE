package com.cdbd.opensource.application.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FailedEventMessage {
    private String originalTopic;
    private String message;
    private int retryCount;
    private String failureReason;
    private long failureTimestamp;

    // 생성자, Getter/Setter 생략 (Lombok 사용 권장)

    public static String toJson(FailedEventMessage failedMessage) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(failedMessage);
    }

    public static FailedEventMessage fromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, FailedEventMessage.class);
    }
}