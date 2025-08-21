package com.cdbd.opensource.presentation.topic;

import com.cdbd.opensource.application.topic.TopicFacade;
import com.cdbd.opensource.domain.topic.Topic;
import com.cdbd.opensource.presentation.eventlog.PageRequestDto;
import com.cdbd.opensource.presentation.eventlog.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/config/kafka")
public class TopicController {
    private final TopicFacade facade;

    @GetMapping(value = "/topics", produces = "application/json")
    public ResponseEntity<PageResponseDto<Topic>> getTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        try {
            PageRequestDto pageRequest = PageRequestDto.builder()
                    .page(page)
                    .size(size)
                    .sortBy(sortBy)
                    .direction(direction)
                    .build();

            PageResponseDto<Topic> dto = facade.getTopics(pageRequest);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            // 예외가 발생하면 로그를 출력하고, 적절한 에러 응답을 반환
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/update")
    public ResponseEntity updateTopic(@RequestBody TopicRequest request) {
        facade.updateTopic(request.toTopicCommand());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity createTopic(@RequestBody TopicRequest request) {
        facade.createTopic(request.toTopicCommand());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteTopic(Long id) {
        facade.deleteTopic(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{topicName}/test-connection")
    public ResponseEntity<ConnectionTestResult> testTopicConnection(@PathVariable String topicName) {
        ConnectionTestResult result = facade.testTopicConnection(topicName);
        return ResponseEntity.ok(result);
    }

}
