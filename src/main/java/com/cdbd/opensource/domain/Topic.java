package com.cdbd.opensource.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Topic {
    private Long id;
    private String topic;
    private int partitionCount;
    private String description;
}
