package com.chris.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chris.aws.s3")
@Data
public class S3Properties {
    private String bucket;
    private String region;
    private String accessKeyId;
    private String secretAccessKey;
}
