package com.cdbd.opensource.application;

import com.cdbd.opensource.infrastructure.DynamicSubscriptionRepository;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Transactional(readOnly = true)
public class DynamicMessageSubscribeHelper {

    private final DynamicSubscriptionRepository dynamicSubscriptionRepository;
    private final DynamicMessageListener dynamicMessageListener;
    private final KafkaConsumer<String, Object> mailKafkaConsumer;

    private KafkaListener listener;
    private final Set<String> topics = ConcurrentHashMap.newKeySet();

    public DynamicMessageSubscribeHelper(
            DynamicSubscriptionRepository dynamicSubscriptionRepository,
            DynamicMessageListener dynamicMessageListener,
            KafkaConsumer<String, Object> mailKafkaConsumer
    ) {
        this.dynamicSubscriptionRepository = dynamicSubscriptionRepository;
        this.dynamicMessageListener = dynamicMessageListener;
        this.mailKafkaConsumer = mailKafkaConsumer;
    }

    @PostConstruct
    public void run() {
        Set<String> allTopics = dynamicSubscriptionRepository.findAllTopics();
        topics.addAll(allTopics);
        listener = new KafkaListener(mailKafkaConsumer, dynamicMessageListener, topics);
        new Thread(listener).start();
    }

    @PreDestroy
    public void destroy() {
        if (listener != null) {
            listener.stop();
        }
        if (mailKafkaConsumer != null) {
            mailKafkaConsumer.close();
        }
    }

    public void subscribe(String topic) {
        topics.add(topic);
    }

    private static class KafkaListener implements Runnable {

        private final KafkaConsumer<String, Object> mailKafkaConsumer;
        private final DynamicMessageListener dynamicMessageListener;
        private final Set<String> topics;

        private volatile boolean isRunning = false;

        public KafkaListener(
                KafkaConsumer<String, Object> mailKafkaConsumer,
                DynamicMessageListener dynamicMessageListener,
                Set<String> topics
        ) {
            this.mailKafkaConsumer = mailKafkaConsumer;
            this.dynamicMessageListener = dynamicMessageListener;
            this.topics = topics;
        }

        public void stop() {
            isRunning = false;
        }

        @Override
        public void run() {
            isRunning = true;
            while (isRunning) {
                try {
                    if (topics.isEmpty()) {
                        Thread.sleep(5000);
                        continue;
                    }

                    mailKafkaConsumer.subscribe(topics);
                    Duration pollTimeout = Duration.ofSeconds(5);
                    mailKafkaConsumer.poll(pollTimeout)
                            .forEach(record -> {
                                dynamicMessageListener.send(record.topic(), record.value().toString());
                            });

                } catch (Exception e) {
                }
            }
        }
    }
}