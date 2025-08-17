package com.cdbd.opensource.domain;

import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;

import java.util.Set;

public interface TopicRepository {
    Set<String> findAllTopics();
    PageResponseDto<Topic> getTopics(PageRequestDto pageRequest);
}
