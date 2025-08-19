package com.cdbd.opensource.infrastructure;

import com.cdbd.opensource.domain.Topic;
import com.cdbd.opensource.domain.TopicRepository;
import com.cdbd.opensource.infrastructure.jpa.JpaTopicRepository;
import com.cdbd.opensource.infrastructure.jpa.TopicEntity;
import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TopicRepositoryImpl implements TopicRepository {
    private final JpaTopicRepository topicRepository;

    @Override
    public Set<String> findAllTopics() {
        return topicRepository.findAll().stream().map(TopicEntity::getTopic).collect(Collectors.toSet());
    }

    @Override
    public PageResponseDto<Topic> getTopics(PageRequestDto pageRequest) {
        Sort.Direction direction = "asc".equalsIgnoreCase(pageRequest.getDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), Sort.by(direction, pageRequest.getSortBy()));
        Page<TopicEntity> logEntityPage = topicRepository.findAll(pageable);

        List<Topic> topics = logEntityPage.getContent().stream().map(TopicEntity::toTopic).toList();

        return PageResponseDto.<Topic>builder()
                .content(topics)
                .currentPage(logEntityPage.getNumber())
                .pageSize(logEntityPage.getSize())
                .totalElements(logEntityPage.getTotalElements())
                .totalPages(logEntityPage.getTotalPages())
                .build();
    }

    @Override
    public void update(Topic topic) {
        Optional<TopicEntity> existingTopicOptional = topicRepository.findById(topic.getId());
        if (existingTopicOptional.isEmpty()) return;
        TopicEntity entity = new TopicEntity();
        entity.setId(topic.getId());
        entity.setTopic(topic.getTopic());
        entity.setPartitionCount(topic.getPartitionCount());
        entity.setDescription(topic.getDescription());
        topicRepository.save(entity);
    }

    @Override
    public void save(Topic topic) {
        TopicEntity entity = new TopicEntity();
        entity.setId(topic.getId());
        entity.setTopic(topic.getTopic());
        entity.setPartitionCount(topic.getPartitionCount());
        entity.setDescription(topic.getDescription());
        topicRepository.save(entity);
    }

    @Override
    public void delete(Long id) {
        Optional<TopicEntity> entity = topicRepository.findById(id);
        if (entity.isEmpty()) return;
        topicRepository.delete(entity.get());
    }
}
