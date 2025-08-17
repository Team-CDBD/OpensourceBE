package com.cdbd.opensource.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
	
	@Bean(name = "notificationExecutor")
	ThreadPoolTaskExecutor notificationExecutor() {
		ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
		exec.setCorePoolSize(1);
		exec.setMaxPoolSize(1);
		exec.setQueueCapacity(10_000);
		exec.setThreadNamePrefix("notif-");
		exec.initialize();
		return exec;
	}
	
}
