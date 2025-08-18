package com.cdbd.opensource.infrastructure;

import com.cdbd.opensource.domain.Topic;
import com.cdbd.opensource.infrastructure.jpa.JpaTopicRepository;
import com.cdbd.opensource.infrastructure.jpa.TopicEntity;
import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicRepositoryImplTest {

    @Mock
    private JpaTopicRepository jpaRepository;

    private TopicRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new TopicRepositoryImpl(jpaRepository);
    }

    @Test
    void getTopics_페이지요청을_JPA페이지로_변환하여_조회() {
        // given
        PageRequestDto pageRequest = PageRequestDto.builder()
                .page(0)
                .size(10)
                .sortBy("id")
                .direction("asc")
                .build();

        TopicEntity entity = TopicEntity.builder()
                .id(1L)
                .name("test-topic")
                .description("test description")
                .build();
        
        Page<TopicEntity> entityPage = new PageImpl<>(List.of(entity));
        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(entityPage);

        // when
        PageResponseDto<Topic> result = repository.getTopics(pageRequest);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getName()).isEqualTo("test-topic");
        verify(jpaRepository).findAll(any(Pageable.class));
    }

    @Test
    void save_도메인객체를_엔티티로_변환하여_저장() {
        // given
        Topic topic = new Topic(null, "test-topic", "test description");
        TopicEntity savedEntity = TopicEntity.builder()
                .id(1L)
                .name("test-topic")
                .description("test description")
                .build();
        
        when(jpaRepository.save(any(TopicEntity.class))).thenReturn(savedEntity);

        // when
        repository.save(topic);

        // then
        verify(jpaRepository).save(any(TopicEntity.class));
    }

    @Test
    void update_기존엔티티를_찾아서_업데이트() {
        // given
        Topic topic = new Topic(1L, "updated-topic", "updated description");
        TopicEntity existingEntity = TopicEntity.builder()
                .id(1L)
                .name("old-topic")
                .description("old description")
                .build();
        
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(existingEntity));

        // when
        repository.update(topic);

        // then
        verify(jpaRepository).findById(1L);
        verify(jpaRepository).save(any(TopicEntity.class));
    }

    @Test
    void delete_ID로_엔티티_삭제() {
        // given
        Long topicId = 1L;

        // when
        repository.delete(topicId);

        // then
        verify(jpaRepository).deleteById(topicId);
    }
}