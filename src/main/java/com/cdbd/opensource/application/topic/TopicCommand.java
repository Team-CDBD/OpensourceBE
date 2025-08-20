package com.cdbd.opensource.application.topic;

import com.cdbd.opensource.domain.topic.Topic;
import lombok.Builder;

@Builder
public record TopicCommand(
        Long id,
        String topic,
        int partitionCount,
        String description
) {
    public Topic toTopic() {
        return new Topic(
                id,
                topic,
                partitionCount,
                description
        );
    }
}
