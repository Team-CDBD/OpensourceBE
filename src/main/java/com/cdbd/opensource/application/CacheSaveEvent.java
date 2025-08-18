package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.EventLog;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class CacheSaveEvent {
    private final EventLog eventLog;
}