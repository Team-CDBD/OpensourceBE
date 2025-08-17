package com.cdbd.opensource.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class EventLog {
    private String className;
    private String method;
    private int line;
    private String message;
    private String severity;
    private List<String> futureCalls;
    private String topic;
}
