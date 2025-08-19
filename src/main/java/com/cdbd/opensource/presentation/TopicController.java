package com.cdbd.opensource.presentation;

import com.cdbd.opensource.application.TopicFacade;
import com.cdbd.opensource.domain.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000") // 이 컨트롤러의 모든 메서드에 적용
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

        System.out.println(pageRequest);

        return ResponseEntity.ok(facade.getTopics(pageRequest));
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
