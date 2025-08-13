package com.cdbd.opensource.application.notification;

import org.springframework.stereotype.Service;

import com.cdbd.opensource.infrastructure.notification.ChannelSettingManager;
import com.cdbd.opensource.infrastructure.notification.entity.ChannelSetting;
import com.cdbd.opensource.infrastructure.notification.repository.ChannelSettingRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelSettingService {
	private final ChannelSettingRepository repository;
	private final ChannelSettingManager manager;
	
	@Transactional
	public ChannelSetting create(String botName, String channelType, String settings) {
		// 1. 채널 세팅 저장
		ChannelSetting channelSetting = ChannelSetting.builder()
				.botName(botName)
				.channelType(channelType)
				.settings(settings)
				.build();
		
		// 2. 채널 세팅 reload
		manager.reload();
		
		return repository.save(channelSetting);
	}
}
