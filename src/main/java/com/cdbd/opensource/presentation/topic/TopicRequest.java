package com.cdbd.opensource.presentation.topic;

import com.cdbd.opensource.application.topic.TopicCommand;
import lombok.Builder;

@Builder
public record TopicRequest (
        Long id,
        String topic,
        int partitionCount,
        String description
) {
    public TopicCommand toTopicCommand() {
        return TopicCommand.builder()
                .id(id)
                .topic(topic)
                .partitionCount(partitionCount)
                .description(description)
                .build();

    }
}
