package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.Topic;
import com.cdbd.opensource.domain.TopicService;
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
class TopicFacadeTest {

    @Mock
    private TopicService topicService;

    private TopicFacade facade;

    @BeforeEach
    void setUp() {
        facade = new TopicFacade(topicService);
    }

    @Test
    void getTopics_페이지요청을_서비스에_전달() {
        // given
        PageRequestDto pageRequest = PageRequestDto.builder()
                .page(0)
                .size(10)
                .sortBy("id")
                .direction("asc")
                .build();
        
        PageResponseDto<Topic> expectedResponse = new PageResponseDto<>(List.of(), 0, 0, 0, 0);
        when(topicService.getTopics(pageRequest)).thenReturn(expectedResponse);

        // when
        PageResponseDto<Topic> result = facade.getTopics(pageRequest);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(topicService).getTopics(pageRequest);
    }

    @Test
    void createTopic_토픽커맨드를_서비스에_전달() {
        // given
        TopicCommand command = TopicCommand.builder()
                .name("test-topic")
                .description("test description")
                .build();

        // when
        facade.createTopic(command);

        // then
        verify(topicService).createTopic(any(Topic.class));
    }

    @Test
    void updateTopic_토픽커맨드를_서비스에_전달() {
        // given
        TopicCommand command = TopicCommand.builder()
                .name("test-topic")
                .description("updated description")
                .build();

        // when
        facade.updateTopic(command);

        // then
        verify(topicService).updateTopic(any(Topic.class));
    }

    @Test
    void deleteTopic_토픽ID를_서비스에_전달() {
        // given
        Long topicId = 1L;

        // when
        facade.deleteTopic(topicId);

        // then
        verify(topicService).deleteTopic(topicId);
    }

    @Test
    void testTopicConnection_토픽이름을_서비스에_전달() {
        // given
        String topicName = "test-topic";
        ConnectionTestResult expectedResult = ConnectionTestResult.builder()
                .topicName(topicName)
                .isConnectable(true)
                .responseTime(100L)
                .partitionCount(3)
                .errorMessage(null)
                .build();
        
        when(topicService.testTopicConnection(topicName)).thenReturn(expectedResult);

        // when
        ConnectionTestResult result = facade.testTopicConnection(topicName);

        // then
        assertThat(result).isEqualTo(expectedResult);
        verify(topicService).testTopicConnection(topicName);
    }
}