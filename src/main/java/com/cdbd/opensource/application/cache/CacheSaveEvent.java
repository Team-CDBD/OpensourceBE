package com.cdbd.opensource.application.cache;

import com.cdbd.opensource.domain.eventlog.EventLog;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class CacheSaveEvent {
    private final EventLog eventLog;
}