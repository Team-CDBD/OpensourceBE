package com.cdbd.opensource.presentation;

import com.cdbd.opensource.application.TopicCommand;
import com.cdbd.opensource.application.TopicFacade;
import com.cdbd.opensource.domain.Topic;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TopicController.class)
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TopicFacade topicFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getTopics_페이지네이션_파라미터로_토픽목록_조회() throws Exception {
        // given
        PageResponseDto<Topic> response = new PageResponseDto<>(List.of(), 0, 0, 0, 0);
        when(topicFacade.getTopics(any(PageRequestDto.class))).thenReturn(response);

        // when & then
        mockMvc.perform(get("/config/kafka/topics")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(topicFacade).getTopics(any(PageRequestDto.class));
    }

    @Test
    void createTopic_토픽생성_요청처리() throws Exception {
        // given
        TopicRequest request = TopicRequest.builder()
                .topic("test-topic")
                .description("test description")
                .build();

        // when & then
        mockMvc.perform(post("/config/kafka/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(topicFacade).createTopic(any(TopicCommand.class));
    }

    @Test
    void updateTopic_토픽수정_요청처리() throws Exception {
        // given
        TopicRequest request = TopicRequest.builder()
                .topic("test-topic")
                .description("updated description")
                .build();

        // when & then
        mockMvc.perform(put("/config/kafka/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(topicFacade).updateTopic(any(TopicCommand.class));
    }

    @Test
    void deleteTopic_토픽삭제_요청처리() throws Exception {
        // given
        Long topicId = 1L;

        // when & then
        mockMvc.perform(delete("/config/kafka/delete")
                .param("id", topicId.toString()))
                .andExpect(status().isOk());

        verify(topicFacade).deleteTopic(topicId);
    }

    @Test
    void testTopicConnection_토픽연결테스트_요청처리() throws Exception {
        // given
        String topicName = "test-topic";
        ConnectionTestResult result = ConnectionTestResult.builder()
                .topicName(topicName)
                .isConnectable(true)
                .responseTime(100L)
                .partitionCount(3)
                .errorMessage(null)
                .build();
        
        when(topicFacade.testTopicConnection(topicName)).thenReturn(result);

        // when & then
        mockMvc.perform(post("/config/kafka/{topicName}/test-connection", topicName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicName").value(topicName))
                .andExpect(jsonPath("$.isConnectable").value(true))
                .andExpect(jsonPath("$.partitionCount").value(3));

        verify(topicFacade).testTopicConnection(topicName);
    }
}