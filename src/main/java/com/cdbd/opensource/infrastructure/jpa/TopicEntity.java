package com.cdbd.opensource.infrastructure.jpa;

import com.cdbd.opensource.domain.Topic;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "topic")
@Getter
@Setter
public class TopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "topic")
    private String topic;

    @Column(name = "partition_count")
    private int partitionCount;

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    public Topic toTopic() {
        return new Topic(
                id,
                topic,
                partitionCount,
                description
        );
    }
}
