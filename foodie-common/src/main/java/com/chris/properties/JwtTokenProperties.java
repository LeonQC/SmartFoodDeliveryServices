package com.chris.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chris.jwt")
@Data
public class JwtTokenProperties {
    private String secretBase64;
    private long accessExpirationSeconds;
    private long refreshExpirationSeconds;
}
