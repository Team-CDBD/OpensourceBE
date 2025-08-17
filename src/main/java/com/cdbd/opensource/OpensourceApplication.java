package com.cdbd.opensource;

import com.cdbd.opensource.infrastructure.llm.AiDefaultsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AiDefaultsProperties.class)
public class OpensourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpensourceApplication.class, args);
	}

}
