package com.cdbd.opensource.application;

import com.cdbd.opensource.domain.Topic;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TopicCommandTest {

    @Test
    void toTopic_커맨드객체를_도메인객체로_변환() {
        // given
        TopicCommand command = TopicCommand.builder()
                .id(1L)
                .topic("test-topic")
                .description("test description")
                .build();

        // when
        Topic result = command.toTopic();

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTopic()).isEqualTo("test-topic");
        assertThat(result.getDescription()).isEqualTo("test description");
    }

    @Test
    void toTopic_ID가_null인경우_처리() {
        // given
        TopicCommand command = TopicCommand.builder()
                .topic("new-topic")
                .description("new description")
                .build();

        // when
        Topic result = command.toTopic();

        // then
        assertThat(result.getId()).isNull();
        assertThat(result.getTopic()).isEqualTo("new-topic");
        assertThat(result.getDescription()).isEqualTo("new description");
    }
}