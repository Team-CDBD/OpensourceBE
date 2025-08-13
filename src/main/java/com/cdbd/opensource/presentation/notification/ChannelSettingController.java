package com.cdbd.opensource.presentation.notification;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdbd.opensource.application.notification.ChannelSettingService;
import com.cdbd.opensource.infrastructure.notification.entity.ChannelSetting;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/channel-settings")
@RequiredArgsConstructor
public class ChannelSettingController {
	private final ChannelSettingService channelSettingService;
	private final ObjectMapper objectMapper;
	
	@PostMapping
	public ResponseEntity<ChannelSetting> create(@RequestBody Map<String, Object> body) {
		String botName = (String) body.get("botName");
		String channelType = (String) body.get("channelType");
		String settings;
		try {
			settings = objectMapper.writeValueAsString(body.get("settings"));
		} catch (Exception e) {
			throw new RuntimeException("Invalid settingsJson format", e);
		} 
		
		ChannelSetting savedChannelSetting = channelSettingService.create(botName, channelType, settings);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(savedChannelSetting);
	}
	
	
}
