package com.chris.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "chris.stripe")
public class StripeProperties {
    /**
     * Stripe 的 Secret Key
     */
    private String secretKey;

    private String webhookSecret;
}
