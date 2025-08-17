package com.cdbd.opensource.application.messagequeue;

import com.cdbd.opensource.infrastructure.DynamicSubscriptionRepository;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Transactional(readOnly = true)
public class DynamicMessageSubscribeHelper {

    private final DynamicSubscriptionRepository dynamicSubscriptionRepository;
    private final DynamicMessageListener dynamicMessageListener;
    private final KafkaConsumer<String, Object> kafkaConsumer;

    private KafkaListener listener;
    private final Set<String> topics = ConcurrentHashMap.newKeySet();

    public DynamicMessageSubscribeHelper(
            DynamicSubscriptionRepository dynamicSubscriptionRepository,
            DynamicMessageListener dynamicMessageListener,
            KafkaConsumer<String, Object> kafkaConsumer
    ) {
        this.dynamicSubscriptionRepository = dynamicSubscriptionRepository;
        this.dynamicMessageListener = dynamicMessageListener;
        this.kafkaConsumer = kafkaConsumer;
    }

    @PostConstruct
    public void run() {
        Set<String> allTopics = dynamicSubscriptionRepository.findAllTopics();
        topics.addAll(allTopics);
        listener = new KafkaListener(kafkaConsumer, dynamicMessageListener, topics);
        new Thread(listener).start();
    }

    @PreDestroy
    public void destroy() {
        if (listener != null) {
            listener.stop();
        }
        if (kafkaConsumer != null) {
            kafkaConsumer.close();
        }
    }

    public void subscribe(String topic) {
        topics.add(topic);
    }

    private static class KafkaListener implements Runnable {

        private final KafkaConsumer<String, Object> kafkaConsumer;
        private final DynamicMessageListener dynamicMessageListener;
        private final Set<String> topics;

        private volatile boolean isRunning = false;

        public KafkaListener(
                KafkaConsumer<String, Object> kafkaConsumer,
                DynamicMessageListener dynamicMessageListener,
                Set<String> topics
        ) {
            this.kafkaConsumer = kafkaConsumer;
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

                    kafkaConsumer.subscribe(topics);
                    Duration pollTimeout = Duration.ofSeconds(5);
                    kafkaConsumer.poll(pollTimeout)
                            .forEach(record -> {
                                dynamicMessageListener.send(record.topic(), record.value());
                            });

                } catch (Exception e) {
                }
            }
        }
    }
}