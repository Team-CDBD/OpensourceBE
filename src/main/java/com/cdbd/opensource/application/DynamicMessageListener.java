package com.cdbd.opensource.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DynamicMessageListener {

    @Async
    public void send(String topic, Object eventLog) {

    }
}