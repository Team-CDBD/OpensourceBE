package com.cdbd.opensource.infrastructure;

import com.cdbd.opensource.infrastructure.jpa.JpaTopicRepository;
import com.cdbd.opensource.infrastructure.jpa.TopicEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DynamicSubscriptionRepository {
    private final JpaTopicRepository topicRepository;

    public Set<String> findAllTopics() {
        return topicRepository.findAll().stream().map(TopicEntity::getTopic).collect(Collectors.toSet());
    }
}
