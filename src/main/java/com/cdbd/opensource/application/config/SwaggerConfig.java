package com.cdbd.opensource.application.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Swagger Board API Document")
                .version("v1.0.0")
                .description("Swagger 게시판 명세서");
        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
