package com.cdbd.opensource.infrastructure.jpa;

import com.cdbd.opensource.domain.topic.Topic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@AllArgsConstructor
@Entity
@Table(name = "topic")
@Getter
@Setter
@NoArgsConstructor
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

    @Column(
            name = "created_at",
            columnDefinition = "timestamp",
            updatable = false
    )
    @CreatedDate
    private LocalDateTime createdAt;

    public Topic toTopic() {
        return new Topic(
                id,
                topic,
                partitionCount,
                description
        );
    }
}
