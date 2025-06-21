package com.chris.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Springdoc + Knife4j 文档 Bean
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartFoodDelivery API 文档")
                        .version("1.0")
                        .description("SmartFoodDelivery RESTful API 描述"));
    }

    @Bean
    public GroupedOpenApi accountApi() {
        return GroupedOpenApi.builder()
                .group("Accounts API")
                .packagesToScan("com.chris.controller.account")
                .build();
    }

    @Bean
    public GroupedOpenApi merchantApi() {
        return GroupedOpenApi.builder()
                .group("Merchants API")
                .packagesToScan("com.chris.controller.merchant")
                .build();
    }

    @Bean
    public GroupedOpenApi clientApi() {
        return GroupedOpenApi.builder()
                .group("Clients API")
                .packagesToScan("com.chris.controller.client")
                .build();
    }

    @Bean
    public GroupedOpenApi riderApi() {
        return GroupedOpenApi.builder()
                .group("Riders API")
                .packagesToScan("com.chris.controller.rider")
                .build();
    }
}
