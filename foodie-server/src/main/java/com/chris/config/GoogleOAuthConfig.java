package com.chris.config;

import com.chris.properties.GoogleOAuthProperties;
import com.chris.utils.GoogleOAuthUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;

import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleOAuthConfig {
    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(GoogleOAuthProperties props) throws GeneralSecurityException, IOException {
        return new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(props.getClientId()))
                .build();
    }

    @Bean
    public GoogleOAuthUtil googleOAuthUtil(GoogleIdTokenVerifier verifier) {
        return new GoogleOAuthUtil(verifier);
    }
}
