package com.cdbd.opensource.domain.topic;

import com.cdbd.opensource.presentation.eventlog.PageRequestDto;
import com.cdbd.opensource.presentation.eventlog.PageResponseDto;

import java.util.Set;

public interface TopicRepository {
    Set<String> findAllTopics();
    PageResponseDto<Topic> getTopics(PageRequestDto pageRequest);

    void update(Topic topic);

    void save(Topic topic);

    void delete(Long id);
}
