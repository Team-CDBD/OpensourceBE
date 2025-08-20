package com.cdbd.opensource.presentation;

import com.cdbd.opensource.infrastructure.llm.AiRuntimeManager;
import com.cdbd.opensource.infrastructure.llm.AiRuntimeSettings;
import com.cdbd.opensource.presentation.dto.AiSettingsRequest;
import com.cdbd.opensource.presentation.dto.AiSettingsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/ai-settings")
public class AiController {

  private final AiRuntimeManager manager;

  public AiController(AiRuntimeManager manager) {
    this.manager = manager;
  }

  @GetMapping
  public ResponseEntity<AiSettingsResponse> getSettings() {
    AiRuntimeSettings s = manager.current();
    return ResponseEntity.ok(
        new AiSettingsResponse(s.maskedApiKey(), s.maxTokens(), s.temperature()));
  }

  @PutMapping
  public ResponseEntity<Void> updateSettings(@RequestBody AiSettingsRequest req) {
    manager.applyUserInputs(req.apiKey(), req.maxOutputTokens(), req.temperature());
    return ResponseEntity.ok().build();
  }

}
