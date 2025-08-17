package com.cdbd.opensource.infrastructure.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "future_call")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FutureCallEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column(name = "call_name")
    private String callName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_log_id")
    private EventLogEntity eventLog;
}