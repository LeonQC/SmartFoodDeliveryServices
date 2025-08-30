package com.chris.config;

import com.chris.properties.GoogleGeocodingProperties;
import com.chris.utils.GoogleMapAPIUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleMapAPIConfig {
    @Bean
    public GoogleMapAPIUtil googleMapAPIUtil(GoogleGeocodingProperties props) {
        return new GoogleMapAPIUtil(props);
    }
}

