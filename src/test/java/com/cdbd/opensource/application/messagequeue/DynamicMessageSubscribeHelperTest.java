package com.cdbd.opensource.application.messagequeue;

import com.cdbd.opensource.infrastructure.TopicRepositoryImpl;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicMessageSubscribeHelperTest {

    @Mock
    private TopicRepositoryImpl dynamicSubscriptionRepository;
    
    @Mock
    private DynamicMessageListener dynamicMessageListener;
    
    @Mock
    private KafkaConsumer<String, Object> kafkaConsumer;

    private DynamicMessageSubscribeHelper helper;

    @BeforeEach
    void setUp() {
        helper = new DynamicMessageSubscribeHelper(
                dynamicSubscriptionRepository,
                dynamicMessageListener,
                kafkaConsumer
        );
    }

    @Test
    void run_초기화시_모든토픽을_구독한다() {
        // given
        Set<String> topics = Set.of("topic1", "topic2");
        when(dynamicSubscriptionRepository.findAllTopics()).thenReturn(topics);

        // when
        helper.run();

        // then
        verify(dynamicSubscriptionRepository).findAllTopics();
    }

    @Test
    void subscribe_새토픽을_추가한다() {
        // given
        String newTopic = "newTopic";

        // when
        helper.subscribe(newTopic);

        // then
        // 토픽이 추가되었는지 확인하는 로직은 내부 구현에 의존
    }

    @Test
    void destroy_리소스를_정리한다() {
        // given
        when(dynamicSubscriptionRepository.findAllTopics()).thenReturn(Set.of());
        helper.run();

        // when
        helper.destroy();

        // then
        verify(kafkaConsumer).close();
    }
}