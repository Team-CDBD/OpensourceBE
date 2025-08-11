package com.cdbd.opensource.application;

import org.springframework.stereotype.Component;

@Component
@Transactional(readOnly = true)
public class DynamicMessageListener {

    @Async
    public void listen(String topic, String message) {

    }

    public void send(String topic, String string) {
    }
}