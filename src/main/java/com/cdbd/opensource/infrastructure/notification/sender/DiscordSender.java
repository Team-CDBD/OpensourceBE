package com.cdbd.opensource.infrastructure.notification.sender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cdbd.opensource.application.notification.ChannelSettingService;
import com.cdbd.opensource.domain.notification.Notification;
import com.cdbd.opensource.domain.notification.NotificationSender;
import com.cdbd.opensource.infrastructure.notification.ChannelSettingManager;
import com.cdbd.opensource.infrastructure.notification.entity.ChannelSetting;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("DISCORD")
public class DiscordSender implements NotificationSender{
	
	private String url;
	private String username;
	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void init(ChannelSetting channelSetting) throws Exception {
		Map<String, Object> setting = objectMapper.readValue(
				channelSetting.getSettings(),
				new TypeReference<Map<String, Object>>(){}
				);
		
		this.url = (String) setting.get("webhookUrl");
		this.username = channelSetting.getBotName();
	}
	
	@Override
	public void send(Notification notification) throws Exception {
        Map<String, Object> embed = new HashMap<>();
        embed.put("title", notification.getTitle());
        embed.put("description", notification.getBody());
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("embeds", List.of(embed));
        
        this.restTemplate.postForEntity(url, payload, String.class);
	}
}