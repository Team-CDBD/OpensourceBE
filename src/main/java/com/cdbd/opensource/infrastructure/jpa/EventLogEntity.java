package com.cdbd.opensource.infrastructure.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EventLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(
            name = "class_name"
    )
    private String className;

    @Column(
            name = "method"
    )
    private String method;

    @Column(
            name = "line"
    )
    private int line;

    @Column(
            name = "message",
            columnDefinition = "TEXT"
    )
    private String message;

    @Column(
            name = "severity"
    )
    private String severity;

    @Column(
            name = "topic"
    )
    private String topic;

    @Column(
            name = "result",
            columnDefinition = "TEXT"
    )
    private String result;
}
