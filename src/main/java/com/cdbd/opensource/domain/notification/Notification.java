package com.cdbd.opensource.domain.notification;


import com.cdbd.opensource.infrastructure.notification.entity.ChannelSetting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notification {
	private String title;
	private String body;
	private NotificationStatus status = NotificationStatus.READY;
	private ChannelSetting channelSetting;
	private NotificationChannelType channelType;
	private int retryCount = 0;
	private final int maxRetryCount = 3;
	
	public void markRetrying() {
		this.status = NotificationStatus.RETRYING;
	}
	
	public void markSent() {
		this.status = NotificationStatus.SENT;
	}
	
	public void markFailed() {
		this.status = NotificationStatus.FAILED;
	}
	
	public boolean canRetry() {
		return retryCount < maxRetryCount;
	}
	
	public void increaseRetry() {
		this.retryCount++;
	}
}
