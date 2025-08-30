package com.chris.service.impl;

import com.chris.constant.OrderStatusConstant;
import com.chris.constant.RiderAssignStatusConstant;
import com.chris.dto.OrderCompletedEvent;
import com.chris.entity.Order;
import com.chris.entity.OrderStatusLog;
import com.chris.entity.RiderAssignment;
import com.chris.repository.OrderStatusLogRepository;
import com.chris.repository.RiderAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderEventService {

    @Autowired
    private OrderStatusLogRepository orderStatusLogRepository;
    @Autowired
    private RiderAssignmentRepository riderAssignmentRepository;

    public OrderCompletedEvent buildOrderCompletedEvent(Order order) {
        // 1. 查询并按toStatus聚合日志
        List<OrderStatusLog> logs = orderStatusLogRepository.findAllByOrderOrderId(order.getOrderId());
        Map<Short, OrderStatusLog> logMap = logs.stream()
                .filter(l -> l.getToStatus() != null)
                .collect(Collectors.toMap(OrderStatusLog::getToStatus, l -> l, (a, b) -> b)); // 若同状态取最后一次

        List<RiderAssignment> assignments = riderAssignmentRepository.findAllByOrderOrderId(order.getOrderId());

        RiderAssignment acceptedAssignment = assignments.stream()
                .filter(a -> a.getStatus() == RiderAssignStatusConstant.ACCEPTED)
                .findFirst()
                .orElse(null);

        Long riderUserId = acceptedAssignment != null && acceptedAssignment.getRider() != null
                ? acceptedAssignment.getRider().getUser().getUserId()
                : null;

        // 2. 准备各节点时间
        Long createTime = getLogTime(logMap, OrderStatusConstant.PENDING);          // 0
        Long payTime = getLogTime(logMap, OrderStatusConstant.PAID);                // 1
        Long merchantAcceptTime = getLogTime(logMap, OrderStatusConstant.ACCEPTED); // 2
        Long prepareTime = getLogTime(logMap, OrderStatusConstant.READY);           // 3
        Long riderAcceptTime = getLogTime(logMap, OrderStatusConstant.PICKING_UP);  // 4
        Long pickUpTime = getLogTime(logMap, OrderStatusConstant.DISPATCHING);      // 5
        Long completeTime = getLogTime(logMap, OrderStatusConstant.COMPLETED);      // 6

        // 3. 构造事件对象
        OrderCompletedEvent event = new OrderCompletedEvent();
        event.setOrderId(order.getOrderId());
        event.setClientUserId(order.getClient().getUser().getUserId());
        event.setMerchantUserId(order.getMerchant().getUser().getUserId());
        event.setRiderUserId(riderUserId);

        event.setCreateTime(createTime);
        event.setPayTime(payTime);
        event.setMerchantAcceptTime(merchantAcceptTime);
        event.setPrepareTime(prepareTime);
        event.setRiderAcceptTime(riderAcceptTime);
        event.setPickUpTime(pickUpTime);
        event.setCompleteTime(completeTime);

        // 4. 计算各阶段耗时（空值处理，防止NPE）
        event.setPayDuration(safeDiff(payTime, createTime));                    // 创建->支付
        event.setMerchantAcceptDuration(safeDiff(merchantAcceptTime, payTime)); // 支付->商家接单
        event.setPrepareDuration(safeDiff(prepareTime, merchantAcceptTime));    // 商家接单->备餐完成
        event.setWaitRiderDuration(safeDiff(riderAcceptTime, prepareTime));     // 备餐完成->骑手接单
        event.setPickUpDuration(safeDiff(pickUpTime, riderAcceptTime));         // 骑手接单->取餐
        event.setDeliveryDuration(safeDiff(completeTime, pickUpTime));          // 取餐->送达
        event.setTotalDuration(safeDiff(completeTime, createTime));             // 创建->完成

        return event;
    }

    // 安全取出log时间
    private Long getLogTime(Map<Short, OrderStatusLog> map, short status) {
        OrderStatusLog log = map.get(status);
        ZoneId chicago = ZoneId.of("America/Chicago");
        return log != null && log.getChangedAt() != null ? log.getChangedAt().atZone(chicago).toInstant().toEpochMilli() : null;
    }

    // 安全计算差值
    private Long safeDiff(Long later, Long earlier) {
        return (later != null && earlier != null) ? (later - earlier) : null;
    }
}