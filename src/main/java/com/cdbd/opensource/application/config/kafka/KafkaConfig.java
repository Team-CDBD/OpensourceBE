package com.cdbd.opensource.application.config.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaConsumer<String, Object> kafkaConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "dynamic-consumer-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");
        
        return new KafkaConsumer<>(props);
    }

    @Bean
    public AdminClient adminClient() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("request.timeout.ms", 5000);
        props.put("connections.max.idle.ms", 10000);
        
        return AdminClient.create(props);
    }
}
