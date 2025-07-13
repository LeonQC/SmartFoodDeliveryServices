package com.chris.controller.common;

import com.chris.service.OrderService;
import com.chris.utils.StripeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
public class StripeController {
    @Autowired
    private StripeUtil stripeUtil;
    @Autowired
    private OrderService orderService;

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request, @RequestBody String payload) {
        String sigHeader = request.getHeader("Stripe-Signature");
        Event event;

        try {
            event = stripeUtil.parseWebhookEvent(payload, sigHeader);
            System.out.println("Stripe 事件类型: " + event.getType());
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(400).body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Webhook error: " + e.getMessage());
        }

        // 推荐：用JsonNode读取所有字段（最保险）
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);

            // 只处理 payment_intent.succeeded 事件
            if ("payment_intent.succeeded".equals(event.getType())) {
                JsonNode dataObject = rootNode.path("data").path("object");
                String paymentIntentId = dataObject.path("id").asText();
                String orderIdStr = dataObject.path("metadata").path("orderId").asText(null);
                String userIdStr = dataObject.path("metadata").path("userId").asText(null);
                long created = dataObject.path("created").asLong(0);
                String paymentMethod = "card";
                if (dataObject.path("payment_method_types").isArray() && !dataObject.path("payment_method_types").isEmpty()) {
                    paymentMethod = dataObject.path("payment_method_types").get(0).asText("card");
                }

                if (orderIdStr != null && userIdStr != null) {
                    LocalDateTime paidAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(created), ZoneId.systemDefault());
                    Long orderId = Long.parseLong(orderIdStr);
                    Long userId = Long.parseLong(userIdStr);

                    System.out.println("paidAt: " + paidAt);
                    orderService.markOrderPaid(orderId, userId, paidAt, paymentMethod, paymentIntentId);
                    System.out.println("订单已写库: orderId=" + orderId + ", userId=" + userId);
                }
            }
        } catch (Exception e) {
            System.err.println("处理 webhook 时异常: " + e.getMessage());
        }

        return ResponseEntity.ok("success");
    }
}

