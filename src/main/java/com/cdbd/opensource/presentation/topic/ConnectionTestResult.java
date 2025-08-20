package com.cdbd.opensource.presentation.topic;

import lombok.Builder;

@Builder
public record ConnectionTestResult(
        String topicName,
        boolean isConnectable,
        long responseTime,
        int partitionCount,
        String errorMessage
) {
}