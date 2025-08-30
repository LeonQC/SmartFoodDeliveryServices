package com.chris.producer;

import com.chris.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderRabbitMQProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendOrderDelayMessage(Long orderId) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_DELAY_QUEUE, orderId);
            log.info("订单延迟关单消息已发送，orderId={}", orderId);
        } catch (Exception e) {
            log.error("订单延迟关单消息发送失败，orderId={}", orderId, e);
            // 这里可以决定是否抛出异常，或做降级处理
            // 降级处理方法： 创建补偿表单，将消息存入表中，定时任务处理，retrySendOrderDelayMessage

            /*OrderDelayMessageCompensation compensation = new OrderDelayMessageCompensation();
            compensation.setOrderId(orderId);
            compensation.setRetryCount(0);
            compensation.setStatus(0); // 待补偿
            compensation.setErrorMsg(e.getMessage());
            compensationRepository.save(compensation); // 你的DAO/Repository*/
        }
    }

//    @Scheduled(cron = "0 */5 * * * ?") // 每5分钟执行一次
//    public void retrySendOrderDelayMessages() {
//        List<OrderDelayMessageCompensation> pendingList = compensationRepository.findByStatus(0); // 0:待补偿
//
//        for (OrderDelayMessageCompensation compensation : pendingList) {
//            try {
//                rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_DELAY_QUEUE, compensation.getOrderId());
//                compensation.setStatus(1); // 1:已补偿成功
//                compensation.setErrorMsg(null);
//                log.info("补偿发送MQ成功，orderId={}", compensation.getOrderId());
//            } catch (Exception e) {
//                compensation.setRetryCount(compensation.getRetryCount() + 1);
//                compensation.setErrorMsg(e.getMessage());
//                if (compensation.getRetryCount() >= 5) {
//                    compensation.setStatus(2); // 2:失败，人工处理
//                    log.error("补偿5次仍失败，orderId={}, 需人工处理", compensation.getOrderId());
//                }
//            }
//            compensationRepository.save(compensation);
//        }
//    }
}
