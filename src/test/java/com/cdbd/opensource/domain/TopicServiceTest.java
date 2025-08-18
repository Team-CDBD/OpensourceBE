package com.cdbd.opensource.domain;

import com.cdbd.opensource.presentation.ConnectionTestResult;
import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private KafkaConnectionService kafkaConnectionService;

    private TopicService service;

    @BeforeEach
    void setUp() {
        service = new TopicService(topicRepository, kafkaConnectionService);
    }

    @Test
    void getTopics_페이지요청을_레포지토리에_전달() {
        // given
        PageRequestDto pageRequest = PageRequestDto.builder()
                .page(0)
                .size(10)
                .sortBy("id")
                .direction("asc")
                .build();
        
        PageResponseDto<Topic> expectedResponse = new PageResponseDto<>(List.of(), 0, 0, 0, 0);
        when(topicRepository.getTopics(pageRequest)).thenReturn(expectedResponse);

        // when
        PageResponseDto<Topic> result = service.getTopics(pageRequest);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(topicRepository).getTopics(pageRequest);
    }

    @Test
    void createTopic_토픽을_레포지토리에_저장() {
        // given
        Topic topic = new Topic(1L, "test-topic", "test description");

        // when
        service.createTopic(topic);

        // then
        verify(topicRepository).save(topic);
    }

    @Test
    void updateTopic_토픽을_레포지토리에_업데이트() {
        // given
        Topic topic = new Topic(1L, "test-topic", "updated description");

        // when
        service.updateTopic(topic);

        // then
        verify(topicRepository).update(topic);
    }

    @Test
    void deleteTopic_토픽ID로_레포지토리에서_삭제() {
        // given
        Long topicId = 1L;

        // when
        service.deleteTopic(topicId);

        // then
        verify(topicRepository).delete(topicId);
    }

    @Test
    void testTopicConnection_카프카연결서비스에_위임() {
        // given
        String topicName = "test-topic";
        ConnectionTestResult expectedResult = ConnectionTestResult.builder()
                .topicName(topicName)
                .isConnectable(true)
                .responseTime(100L)
                .partitionCount(3)
                .errorMessage(null)
                .build();
        
        when(kafkaConnectionService.testConnection(topicName)).thenReturn(expectedResult);

        // when
        ConnectionTestResult result = service.testTopicConnection(topicName);

        // then
        assertThat(result).isEqualTo(expectedResult);
        verify(kafkaConnectionService).testConnection(topicName);
    }
}