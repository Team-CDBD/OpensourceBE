package com.cdbd.opensource.domain;

import com.cdbd.opensource.presentation.ConnectionTestResult;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartitionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.KafkaFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConnectionServiceTest {

    @Mock
    private AdminClient adminClient;

    @Mock
    private DescribeTopicsResult describeTopicsResult;

    @Mock
    private TopicDescription topicDescription;

    private KafkaConnectionService service;

    @BeforeEach
    void setUp() {
        service = new KafkaConnectionService(adminClient);
    }

    @Test
    void testConnection_성공시_연결가능결과_반환() throws Exception {
        // given
        String topicName = "test-topic";
        KafkaFuture<TopicDescription> future = KafkaFuture.completedFuture(topicDescription);
        
        when(adminClient.describeTopics(anyList())).thenReturn(describeTopicsResult);
        when(describeTopicsResult.values()).thenReturn(Map.of(topicName, future));
        when(topicDescription.partitions()).thenReturn(Collections.nCopies(3, mock(TopicPartitionInfo.class)));

        // when
        ConnectionTestResult result = service.testConnection(topicName);

        // then
        assertThat(result.topicName()).isEqualTo(topicName);
        assertThat(result.isConnectable()).isTrue();
        assertThat(result.partitionCount()).isEqualTo(3);
        assertThat(result.errorMessage()).isNull();
        assertThat(result.responseTime()).isGreaterThan(0);
    }

    @Test
    void testConnection_실패시_연결불가결과_반환() {
        // given
        String topicName = "non-existent-topic";
        when(adminClient.describeTopics(anyList())).thenThrow(new RuntimeException("Topic not found"));

        // when
        ConnectionTestResult result = service.testConnection(topicName);

        // then
        assertThat(result.topicName()).isEqualTo(topicName);
        assertThat(result.isConnectable()).isFalse();
        assertThat(result.partitionCount()).isEqualTo(0);
        assertThat(result.errorMessage()).isEqualTo("Topic not found");
        assertThat(result.responseTime()).isGreaterThan(0);
    }
}