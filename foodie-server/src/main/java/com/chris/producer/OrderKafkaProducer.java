package com.chris.producer;

import com.chris.dto.OrderCompletedEvent;
import com.chris.dto.OrderPaidEvent;
import com.chris.dto.OrderReadyEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderKafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendOrderPaidEvent(OrderPaidEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            log.info("kafka生产订单信息：{}", json);
            kafkaTemplate.send("order-paid", json);
        } catch (Exception e) {
            log.error("Kafka消息序列化失败", e);
        }
    }

    public void sendOrderReadyEvent(OrderReadyEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            log.info("kafka生产商家备餐完成推送给骑手：{}", json);
            kafkaTemplate.send("order-ready", json);
        } catch (Exception e) {
            log.error("Kafka消息序列化失败", e);
        }
    }
    /**
     * 推送订单完成事件，包含全链路所有节点时间戳和阶段耗时
     */
    public void sendOrderCompletedEvent(OrderCompletedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            log.info("kafka生产订单完成信息：{}", json);
            kafkaTemplate.send("order-completed", json);
        } catch (Exception e) {
            log.error("Kafka消息序列化失败", e);
        }
    }
}