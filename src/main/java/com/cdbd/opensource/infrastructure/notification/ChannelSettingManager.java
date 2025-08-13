package com.cdbd.opensource.infrastructure.notification;

import org.springframework.stereotype.Component;

import com.cdbd.opensource.infrastructure.notification.entity.ChannelSetting;
import com.cdbd.opensource.infrastructure.notification.repository.ChannelSettingRepository;

import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@Component
public class ChannelSettingManager {

    private final ChannelSettingRepository repository;
    private final List<ChannelSetting> cache = new ArrayList<>();

    public ChannelSettingManager(ChannelSettingRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        reload();
    }

    /**
     * DB에서 전체 채널 설정을 읽어와 캐시에 업데이트
     */
    public synchronized void reload() {
        cache.clear();
        cache.addAll(repository.findAll());
    }

    /**
     * 전체 채널 설정 리스트 조회
     */
    public List<ChannelSetting> getAll() {
        return Collections.unmodifiableList(cache);
    }
}
