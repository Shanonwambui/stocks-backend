package com.example.playground1;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;


public class SwaggerConfig {
    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .pathsToMatch("com.example.playground1.controller")
                .build();
    }
}
