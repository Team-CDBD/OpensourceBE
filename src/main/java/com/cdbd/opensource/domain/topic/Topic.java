package com.cdbd.opensource.domain.topic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Topic {
    private Long id;
    private String topic;
    private int partitionCount;
    private String description;
}
