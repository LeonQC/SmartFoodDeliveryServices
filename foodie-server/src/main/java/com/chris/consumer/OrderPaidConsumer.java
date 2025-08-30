package com.chris.consumer;


import com.chris.websocket.MerchantWebSocketServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class OrderPaidConsumer {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    @KafkaListener(topics = "order-paid", groupId = "merchant-service")
    public void onOrderPaid(String message) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(message);
        Long merchantUserId = node.get("merchantUserId").asLong();
        log.info("kafka消费订单支付成功,订单商家ID:{}", merchantUserId);

        MerchantWebSocketServer.sendToMerchant(merchantUserId, message);
    }
}