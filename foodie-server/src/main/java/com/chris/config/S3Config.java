package com.chris.config;

import com.chris.properties.S3Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    @Bean
    public S3Client s3Client(S3Properties props) {
        return S3Client.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(props.getAccessKeyId(), props.getSecretAccessKey())
                        )
                )
                .build();
    }

    @Bean
    public S3BucketClient s3BucketClient(S3Client client, S3Properties props) {
        // 这里把 bucket 和 client 一并装配到包装类里
        return new S3BucketClient(client, props.getBucket());
    }

    /**
     * Java 16+ record 用来做纯数据承载类非常合适，
     * 嵌套声明会被隐式当 static 处理。
     */
    public record S3BucketClient(S3Client client, String bucket) {}

}
