package com.cdbd.opensource.application.topic;

import com.cdbd.opensource.domain.topic.Topic;
import com.cdbd.opensource.domain.topic.TopicService;
import com.cdbd.opensource.presentation.topic.ConnectionTestResult;
import com.cdbd.opensource.presentation.eventlog.PageRequestDto;
import com.cdbd.opensource.presentation.eventlog.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicFacade {
    private final TopicService service;

    public PageResponseDto<Topic> getTopics(PageRequestDto pageRequest) {
        return service.getTopics(pageRequest);
    }

    public void updateTopic(TopicCommand topicCommand) {
        service.updateTopic(topicCommand.toTopic());
    }

    public void createTopic(TopicCommand topicCommand) {
        service.createTopic(topicCommand.toTopic());
    }

    public void deleteTopic(Long id) {
        service.deleteTopic(id);
    }

    public ConnectionTestResult testTopicConnection(String topicName) {
        return service.testTopicConnection(topicName);
    }
}
