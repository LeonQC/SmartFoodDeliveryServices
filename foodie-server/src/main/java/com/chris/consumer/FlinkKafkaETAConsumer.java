package com.chris.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FlinkKafkaETAConsumer {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "flink-ETA-result", groupId = "my-stats-group")
    public void consume(ConsumerRecord<String, String> record) {
        String value = record.value();
        try {
            JsonNode node = objectMapper.readTree(value);

            // // 分支1：clientUserId, avgPayDuration
            if (node.has("clientUserId") && node.has("avgPayDuration")) {
                String clientUserId = node.get("clientUserId").asText();
                String avg = node.get("avgPayDuration").asText();
                String redisKey = String.format("eta:client:%s:pay", clientUserId);
                stringRedisTemplate.opsForValue().set(redisKey, avg, 1, TimeUnit.DAYS);
                return;
            }

            // 分支2: merchantUserId, dayOfWeek, hour, avgAcceptPrepareDuration
            if (node.has("merchantUserId") && node.has("dayOfWeek") && node.has("hour") && node.has("avgAcceptPrepareDuration")) {
                String merchantUserId = node.get("merchantUserId").asText();
                String dayOfWeek = node.get("dayOfWeek").asText();
                String hour = node.get("hour").asText();
                String avg = node.get("avgAcceptPrepareDuration").asText();
                String redisKey = String.format("eta:merchant:%s:%s:%s:acceptPrepare", merchantUserId, dayOfWeek, hour);
                stringRedisTemplate.opsForValue().set(redisKey, avg, 1, TimeUnit.DAYS);
                return;
            }

            // 分支3：merchantId, dayOfWeek, hour, avgWaitRiderDuration
            if (node.has("merchantUserId") && node.has("dayOfWeek") && node.has("hour") && node.has("avgWaitRiderAcceptPickUpDuration")) {
                String merchantUserId = node.get("merchantUserId").asText();
                String dayOfWeek = node.get("dayOfWeek").asText();
                String hour = node.get("hour").asText();
                String avg = node.get("avgWaitRiderAcceptPickUpDuration").asText();
                String redisKey = String.format("eta:merchant:%s:%s:%s:waitRider", merchantUserId, dayOfWeek, hour);
                stringRedisTemplate.opsForValue().set(redisKey, avg, 1, TimeUnit.DAYS);
            }
        } catch (Exception e) {
            log.error("处理 ETA Kafka 消息异常，内容={}", record.value(), e);
        }
    }
}
