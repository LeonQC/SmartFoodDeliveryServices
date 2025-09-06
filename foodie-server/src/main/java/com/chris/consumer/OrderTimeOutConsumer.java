package com.chris.consumer;

import com.chris.config.RabbitMQConfig;
import com.chris.constant.OrderStatusConstant;
import com.chris.constant.PayStatusConstant;
import com.chris.entity.Order;
import com.chris.entity.OrderStatusLog;
import com.chris.exception.OrderNotFoundException;
import com.chris.repository.OrderRepository;
import com.chris.repository.OrderStatusLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.chris.constant.MessageConstant.ORDER_NOT_FOUND;

@Service
@Slf4j
public class OrderTimeOutConsumer {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderStatusLogRepository orderStatusLogRepository;

    @RabbitListener(queues = RabbitMQConfig.ORDER_DEAD_QUEUE)
    public void handleOrderTimeout(Long orderId) {
        log.info("接收到订单超时关单消息，orderId={}", orderId);

        // 1. 查询订单
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        // 2. 幂等校验：如果订单已支付或已关闭，则忽略
        if (OrderStatusConstant.PENDING != order.getStatus() ||
                PayStatusConstant.UNPAID != order.getPayStatus()) {
            log.info("订单已支付或已关闭，无需关单，orderId={}", orderId);
            return;
        }

        // 3. 自动关闭订单
        order.setStatus(OrderStatusConstant.CANCELLED);
        orderRepository.save(order);
        log.info("自动关闭超时未支付订单，orderId={}", orderId);

        // 4. 记录订单状态变更日志、推送通知等
        OrderStatusLog log = new OrderStatusLog();
        log.setOrder(order);
        log.setFromStatus(OrderStatusConstant.PENDING);
        log.setToStatus(OrderStatusConstant.CANCELLED);
        log.setChangedBy("System");
        log.setRemark("订单超时未支付，自动关闭");
        orderStatusLogRepository.save(log);
    }
}
