package com.chris.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    // 订单延迟队列
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    // 死信队列
    public static final String ORDER_DEAD_QUEUE = "order.dead.queue";
    // 死信交换机
    public static final String ORDER_DEAD_EXCHANGE = "order.dead.exchange";
    // 死信路由key
    public static final String ORDER_DEAD_ROUTING_KEY = "order.dead.routingkey";

    // 延迟队列
    @Bean
    public Queue delayQueue() {
        Map<String, Object> args = new HashMap<>();
        // 15分钟=900000毫秒
        args.put("x-message-ttl", 900000);
        args.put("x-dead-letter-exchange", ORDER_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", ORDER_DEAD_ROUTING_KEY);
        return new Queue(ORDER_DELAY_QUEUE, true, false, false, args);
    }

    // 死信队列
    @Bean
    public Queue deadQueue() {
        return new Queue(ORDER_DEAD_QUEUE, true);
    }

    // 死信交换机
    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(ORDER_DEAD_EXCHANGE);
    }

    // 绑定死信队列
    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(deadQueue())
                .to(deadExchange())
                .with(ORDER_DEAD_ROUTING_KEY);
    }
}

