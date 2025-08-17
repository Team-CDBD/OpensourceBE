package com.cdbd.opensource.application.notification;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.cdbd.opensource.domain.notification.Notification;
import com.cdbd.opensource.infrastructure.notification.ChannelSettingManager;
import com.cdbd.opensource.infrastructure.notification.entity.ChannelSetting;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final ChannelSettingManager channelSettingManager;
	private final ApplicationEventPublisher applicationEventPublisher;
	
	public void send(String title, String body) {
		List<ChannelSetting> channels = channelSettingManager.getAll();

		for (ChannelSetting channel : channels) {
			applicationEventPublisher.publishEvent(Notification.builder()
					.title(title)
					.body(body)
					.channelSetting(channel)
					.build());
		}
	}
}
