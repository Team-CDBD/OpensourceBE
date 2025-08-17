package com.cdbd.opensource.domain;

import com.cdbd.opensource.presentation.ConnectionTestResult;
import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository repository;
    private final KafkaConnectionService kafkaConnectionService;
    public PageResponseDto<Topic> getTopics(PageRequestDto pageRequest) {
        return repository.getTopics(pageRequest);
    }

    public void updateTopic(Topic topic) {
        repository.update(topic);
    }

    public void createTopic(Topic topic) {
        repository.save(topic);
    }

    public void deleteTopic(Long id) {
        repository.delete(id);
    }

    public ConnectionTestResult testTopicConnection(String topicName) {
        return kafkaConnectionService.testConnection(topicName);
    }
}
