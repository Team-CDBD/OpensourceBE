package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.Topic;
import com.cdbd.opensource.domain.TopicService;
import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;
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
}
