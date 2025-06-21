package com.chris.config;

import com.chris.json.JacksonObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 将自定义的 JacksonObjectMapper 注册为 Spring Boot 默认使用的 ObjectMapper
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper() {
        return new JacksonObjectMapper();
    }
}
