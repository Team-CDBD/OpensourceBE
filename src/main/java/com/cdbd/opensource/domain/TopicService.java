package com.cdbd.opensource.domain;

import com.cdbd.opensource.presentation.PageRequestDto;
import com.cdbd.opensource.presentation.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository repository;
    public PageResponseDto<Topic> getTopics(PageRequestDto pageRequest) {
        return repository.getTopics(pageRequest);
    }
}
