package com.cdbd.opensource.presentation;

import com.cdbd.opensource.application.EventLogCommand;
import com.cdbd.opensource.application.EventLogFacade;
import com.cdbd.opensource.domain.EventLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventLogController.class)
class EventLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventLogFacade eventLogFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveEventLog_이벤트로그_저장요청_처리() throws Exception {
        // given
        EventLog eventLog = new EventLog(
                "TestClass",
                "testMethod",
                10,
                "test message",
                "ERROR",
                List.of("call1"),
                "original-topic",
                ""
        );

        EventLogCommand command = EventLogCommand.builder()
                .topic("test-topic")
                .eventLog(eventLog)
                .build();

        // when & then
        mockMvc.perform(post("/eventlog/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());

        verify(eventLogFacade).save(any(EventLogCommand.class));
    }
}