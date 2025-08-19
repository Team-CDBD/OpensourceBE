package com.cdbd.opensource.presentation;

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