package com.chris.service.impl;

import com.chris.constant.OrderStatusConstant;
import com.chris.dto.RejectOrderDTO;
import com.chris.entity.*;
import com.chris.exception.OrderNotFoundException;
import com.chris.exception.OrderStatusChangeException;
import com.chris.mapper.OrderDetailMapper;
import com.chris.repository.OrderStatusLogRepository;
import com.chris.repository.OrderRepository;
import com.chris.service.OrderService;
import com.chris.vo.dashboardVOs.DashboardOngoingOrderVO;
import com.chris.vo.dashboardVOs.DashboardOrderMetricsVO;
import com.chris.vo.orderDetailVOs.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.chris.constant.MessageConstant.ORDER_NOT_FOUND;
import static com.chris.constant.MessageConstant.ORDER_STATUS_CHANGE_FAILED;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusLogRepository orderStatusLogRepository;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
    * 用于商家Dashboard的订单及相关数据统计
    */
    @Override
    public DashboardOrderMetricsVO getOrderMetrics(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        // 一次性拉取当天所有订单
        List<Order> todayOrders = orderRepository.findByMerchantUserUserIdAndCreateTimeBetween(userId, start, end);

        // 营业额
        BigDecimal revenue = todayOrders.stream()
                .filter(order -> order.getStatus() != OrderStatusConstant.CANCELLED)
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 有效订单个数
        List<Order> valid = todayOrders.stream()
                .filter(o -> o.getStatus() >= OrderStatusConstant.PAID && o.getStatus() <= OrderStatusConstant.COMPLETED)
                .toList();
        long validOrders = valid.size();

        //平均客单价
        BigDecimal sum = valid.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgPrice = valid.isEmpty()
                ? BigDecimal.ZERO
                : sum.divide(
                BigDecimal.valueOf(valid.size()),
                2,                  // 保留两位小数
                RoundingMode.HALF_UP
        );

        // 新用户数（按 client 去重）
        List<Order> historyOrders = orderRepository.findByMerchantUserUserIdAndCreateTimeBefore(userId, start);

        Set<Long> oldUserIds = historyOrders.stream()
                .map(o -> o.getClient().getUser().getUserId())
                .collect(Collectors.toSet());

        long newUsers = todayOrders.stream()
                .map(o -> o.getClient().getUser().getUserId())
                .filter(id -> !oldUserIds.contains(id))
                .distinct()
                .count();

        // 各状态统计
        long paid      = todayOrders.stream().filter(o -> o.getStatus() == OrderStatusConstant.PAID).count();        // 待接单
        long preparing = todayOrders.stream().filter(o -> o.getStatus() == OrderStatusConstant.ACCEPTED).count();    // 备餐中
        long readyToGo = todayOrders.stream().filter(o -> o.getStatus() == OrderStatusConstant.READY).count();       // 分派骑手
        long pickingUp = todayOrders.stream().filter(o -> o.getStatus() == OrderStatusConstant.PICKING_UP).count();  // 待取餐
        long dispatch  = todayOrders.stream().filter(o -> o.getStatus() == OrderStatusConstant.DISPATCHING).count(); // 配送中
        long completed = todayOrders.stream().filter(o -> o.getStatus() == OrderStatusConstant.COMPLETED).count();   // 完成
        long cancelled = todayOrders.stream().filter(o -> o.getStatus() == OrderStatusConstant.CANCELLED).count();    // 取消
        long total     = todayOrders.size();

        // 完成率
        double completionRate = total == 0 ? 0 : (completed * 100.0 / total);
        completionRate = Math.round(completionRate * 100.0) / 100.0;

        return new DashboardOrderMetricsVO(
                revenue,
                validOrders,
                avgPrice,
                completionRate,
                newUsers,
                paid,
                preparing,
                readyToGo,
                pickingUp,
                dispatch,
                completed,
                cancelled,
                total
        );
    }

    /**
     * 用于商家Dashboard拉取进行中的订单
     */
    @Override
    public List<DashboardOngoingOrderVO> getOngoingOrders(Long userId, Short status) {
        List<Order> orders = orderRepository.findByMerchantUserIdAndStatus(userId, (short)status);

        return orders.stream()
                .map(order -> {
                    // 拼菜品列表，菜品名称x份，逗号隔开
                    String items = order.getItems().stream()
                            .map(oi -> oi.getDish().getName() + " x" + oi.getQuantity())
                            .collect(Collectors.joining(","));

                    // 拼备注,逗号隔开
                    String itemRemarks = order.getItems().stream()
                            .map(OrderItem::getRemark)
                            .collect(Collectors.joining(","));

                    /*// 格式化地址
                    String address = order.getAddress().getAddressLine1()
                            + (order.getAddress().getAddressLine2() != null ? order.getAddress().getAddressLine2() : "");*/

                    // 取最新派单记录的 ID 和时间（仅 dispatching 状态）
                    Long riderAssignmentId = null;
                    LocalDateTime attemptedAt = null;
                    String riderPhone = null;
                    if (status >= OrderStatusConstant.PICKING_UP) {
                        Optional<RiderAssignment> opt = order.getRiderAssignments().stream()
                                .filter(a -> a.getStatus() == OrderStatusConstant.PICKING_UP)
                                .max(Comparator.comparing(RiderAssignment::getAttemptAt));
                        riderAssignmentId = opt.map(RiderAssignment::getRiderAssignmentId).orElse(null);
                        attemptedAt = opt.map(RiderAssignment::getAttemptAt).orElse(null);
                        riderPhone = opt.map(a -> a.getRider().getPhone()).orElse(null);
                    }

                    return new DashboardOngoingOrderVO(
                            order.getOrderId(),
                            order.getClient().getPhone(),
                            items,
                            itemRemarks,
                            order.getPaidAt(),
                            order.getEstimateDeliveryTime(),
                            order.getTotalAmount(),
                            order.getRemark(),
                            order.getStatus(),
                            attemptedAt,
                            riderAssignmentId,
                            riderPhone
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptOrder(Long userId, Long orderId) {
        Order o = orderRepository.findByOrderIdAndMerchantUserUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        if (o.getStatus() != OrderStatusConstant.PAID) {
            throw new OrderStatusChangeException(ORDER_STATUS_CHANGE_FAILED);
        }
        changeStatus(o, OrderStatusConstant.ACCEPTED,"");
    }

    @Override
    @Transactional
    public void rejectOrder(Long userId, Long orderId, RejectOrderDTO reason) {
        Order o = orderRepository.findByOrderIdAndMerchantUserUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        if (o.getStatus() != OrderStatusConstant.PAID) {
            throw new OrderStatusChangeException(ORDER_STATUS_CHANGE_FAILED);
        }
        changeStatus(o, OrderStatusConstant.CANCELLED, reason.getReason());
    }

    @Override
    @Transactional
    public void readyOrder(Long userId, Long orderId) {
        Order o = orderRepository.findByOrderIdAndMerchantUserUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        if (o.getStatus() != OrderStatusConstant.ACCEPTED) {
            throw new OrderStatusChangeException(ORDER_STATUS_CHANGE_FAILED);
        }
        changeStatus(o, OrderStatusConstant.READY,"");
    }

    @Override
    @Transactional
    public OrderDetailVO getOrderDetail(Long userId, Long orderId) {
        Order o = orderRepository.findByOrderIdAndMerchantUserUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        OrderDetailVO vo = orderDetailMapper.toOrderDetailVO(o);
        // 手动补充 riderPhone
        String riderPhone = o.getRiderAssignments().stream()
                // 只看骑手已接单这一步（按你的业务状态改）
                .filter(a -> a.getStatus() == OrderStatusConstant.PICKING_UP)
                // 按时间取最新一条
                .max(Comparator.comparing(RiderAssignment::getAttemptAt))
                // 拿出 Rider -> phone
                .map(RiderAssignment::getRider)
                .map(Rider::getPhone)
                .orElse(null);

        vo.setRiderPhone(riderPhone);
        return vo;
    }

    /**
     * 公共的状态切换 + 日志记录，此三种操作只能由merchant完成，拒单时传入商家填写的拒单理由进入log表的remark字段
     */
    private void changeStatus(Order o, Short newStatus, String remark) {
        Short oldStatus = o.getStatus();
        o.setStatus(newStatus);
        orderRepository.save(o);
        // 记录日志
        OrderStatusLog log = new OrderStatusLog();
        log.setOrder(o);
        log.setFromStatus(oldStatus);
        log.setToStatus(newStatus);
        log.setChangedBy("merchant:" + o.getMerchant().getMerchantName());
        if (newStatus == OrderStatusConstant.CANCELLED) {
            log.setRemark(remark);
        }
        orderStatusLogRepository.save(log);
    }
}
