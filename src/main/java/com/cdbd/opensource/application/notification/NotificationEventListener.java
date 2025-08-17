package com.cdbd.opensource.application.notification;

import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.cdbd.opensource.domain.notification.Notification;
import com.cdbd.opensource.domain.notification.NotificationSender;
import com.cdbd.opensource.infrastructure.notification.entity.ChannelSetting;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

	private final Map<String, NotificationSender> senderByName;
	private final ApplicationEventPublisher applicationEventPublisher;
	
	@Async("notificationExecutor")
	@EventListener
	public void handle(Notification notification) {
		try {
			ChannelSetting channelSetting = notification.getChannelSetting();
			NotificationSender notificationSender = senderByName.get(channelSetting.getChannelType());
			// 1. 구현체 채널 세팅
			notificationSender.init(channelSetting);
			// 2. 알림 전송
			notificationSender.send(notification);
		} catch(Exception e){
			if (notification.canRetry()) {
				notification.increaseRetry();
				applicationEventPublisher.publishEvent(notification);
			}
		}
	}
}
