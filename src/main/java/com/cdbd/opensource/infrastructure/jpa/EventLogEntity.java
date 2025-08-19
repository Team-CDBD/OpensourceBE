package com.cdbd.opensource.infrastructure.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    // 이 필드를 추가합니다.
    @OneToMany(mappedBy = "eventLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FutureCallEntity> futureCalls;

    // 편의 메서드를 추가하여 관계를 양쪽으로 동기화합니다.
    public void addFutureCall(FutureCallEntity futureCall) {
        this.futureCalls.add(futureCall);
        futureCall.setEventLog(this);
    }
}