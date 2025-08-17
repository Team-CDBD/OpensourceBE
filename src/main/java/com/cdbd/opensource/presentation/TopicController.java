package com.cdbd.opensource.presentation;

import com.cdbd.opensource.application.TopicFacade;
import com.cdbd.opensource.domain.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/config/kafka")
public class TopicController {
    private final TopicFacade facade;

    @GetMapping("/topics")
    public ResponseEntity<PageResponseDto<Topic>> getTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        PageRequestDto pageRequest = PageRequestDto.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();

        return ResponseEntity.ok(facade.getTopics(pageRequest));
    }

    @PutMapping("/update")
    public ResponseEntity updateTopic(@RequestBody TopicRequest request) {
        facade.updateTopic(request.toTopicCommand());
        return ResponseEntity.ok().build();
    }
}
