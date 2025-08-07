package com.chris.config;

import com.chris.properties.GoogleGeocodingProperties;
import com.chris.utils.GoogleGeocodingUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleGeocodingConfig {
    @Bean
    public GoogleGeocodingUtil googleGeocodingUtil(GoogleGeocodingProperties props) {
        return new GoogleGeocodingUtil(props);
    }
}
