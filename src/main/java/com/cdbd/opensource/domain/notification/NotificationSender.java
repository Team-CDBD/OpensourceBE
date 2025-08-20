package com.cdbd.opensource.domain.notification;

import com.cdbd.opensource.infrastructure.notification.entity.ChannelSetting;

public interface NotificationSender {
	
	void init(ChannelSetting channelSetting) throws Exception;
	void send(Notification notification) throws Exception;
}
