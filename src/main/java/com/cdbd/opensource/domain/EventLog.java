package com.cdbd.opensource.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class EventLog {
    private String className;
    private String method;
    private int line;
    private String message;
    private String severity;
    private List<String> futureCalls;
}
