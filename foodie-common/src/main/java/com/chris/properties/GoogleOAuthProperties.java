package com.chris.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chris.google.oauth")
@Data
public class GoogleOAuthProperties {
    private String clientId;
}

