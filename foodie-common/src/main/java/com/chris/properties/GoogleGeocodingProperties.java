package com.chris.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chris.google.maps")
@Data
public class GoogleGeocodingProperties {
    private String apiKey;
}
