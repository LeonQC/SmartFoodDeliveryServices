package com.chris.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

@Configuration
public class RedisTemplateConfig {

    // 1. Spring Data Redis 单机 StringRedisTemplate
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);   // 用 spring.data.redis 的
    }

    // 2. Redisson
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() throws IOException {
        Config config = Config.fromYAML(ResourceUtils.getFile("classpath:redisson.yml"));
        return Redisson.create(config);           // 只连 redis-sentinel哨兵集群
    }
}