package com.chris.consumer;

import com.chris.websocket.RiderWebSocketServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class OrderReadyConsumer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "order-ready", groupId = "rider-service")
    public void onOrderReady(String message) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(message);
        JsonNode listNode = node.get("riderUserIdList");
        if (listNode != null && listNode.isArray()) {
            for (JsonNode riderIdNode : listNode) {
                Long riderUserId = riderIdNode.asLong();
                log.info("kafka消费商家备餐完成, 推送给骑手ID:{}", riderUserId);
                RiderWebSocketServer.sendToRider(riderUserId, message);
            }
        } else {
            log.warn("order-ready消息未包含riderUserIdList字段: {}", message);
        }
    }
}
