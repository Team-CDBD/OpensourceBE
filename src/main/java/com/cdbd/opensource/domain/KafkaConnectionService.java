package com.cdbd.opensource.domain;

import com.cdbd.opensource.presentation.ConnectionTestResult;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KafkaConnectionService {
    
    private final AdminClient adminClient;

    public ConnectionTestResult testConnection(String topicName) {
        long startTime = System.currentTimeMillis();
        
        try {
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singletonList(topicName));
            TopicDescription description = result.values().get(topicName).get(5, TimeUnit.SECONDS);
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            return ConnectionTestResult.builder()
                    .topicName(topicName)
                    .isConnectable(true)
                    .responseTime(responseTime)
                    .partitionCount(description.partitions().size())
                    .errorMessage(null)
                    .build();
                    
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            
            return ConnectionTestResult.builder()
                    .topicName(topicName)
                    .isConnectable(false)
                    .responseTime(responseTime)
                    .partitionCount(0)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}