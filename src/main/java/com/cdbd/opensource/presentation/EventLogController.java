package com.cdbd.opensource.presentation;

import com.cdbd.opensource.application.EventLogFacade;
import com.cdbd.opensource.domain.EventLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EventLogController {
    private final EventLogFacade eventLogFacade;

    @GetMapping("/tables")
    public ResponseEntity<PageResponseDto<EventLog>> getEventLogs(
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

        PageResponseDto<EventLog> responseDto = eventLogFacade.getEventLogs(pageRequest);
        return ResponseEntity.ok(responseDto);
    }
}
