package com.cdbd.opensource.infrastructure.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cdbd.opensource.infrastructure.notification.entity.ChannelSetting;

@Repository
public interface ChannelSettingRepository extends JpaRepository<ChannelSetting, Long>{
}
