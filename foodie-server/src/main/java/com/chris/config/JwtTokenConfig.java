package com.chris.config;

import com.chris.properties.JwtTokenProperties;
import com.chris.utils.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtTokenConfig {
    @Bean
    public JwtTokenUtil jwtTokenUtil(JwtTokenProperties props) {
        return new JwtTokenUtil(
                props.getSecretBase64(),
                props.getAccessExpirationSeconds(),
                props.getRefreshExpirationSeconds()
        );
    }
}
