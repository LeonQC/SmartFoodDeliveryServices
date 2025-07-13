package com.chris.service.impl;

import com.chris.constant.OrderStatusConstant;
import com.chris.constant.PayStatusConstant;
import com.chris.dto.*;
import com.chris.dto.groups.ClientOrderDTO;
import com.chris.entity.*;
import com.chris.exception.*;
import com.chris.mapper.OrderDetailMapper;
import com.chris.producer.OrderEventProducer;
import com.chris.repository.*;
import com.chris.service.OrderService;
import com.chris.utils.DeliveryFeeUtil;
import com.chris.utils.GeoUtil;
import com.chris.utils.GoogleGeocodingUtil;
import com.chris.utils.StripeUtil;
import com.chris.vo.ClientOrderVO;
import com.chris.vo.OrderPayVO;
import com.chris.vo.OrderSubmitVO;
import com.chris.vo.dashboardVOs.DashboardOngoingOrderVO;
import com.chris.vo.dashboardVOs.DashboardOrderMetricsVO;
import com.chris.vo.orderDetailVOs.ClientOrderDetailVO;
import com.chris.vo.orderDetailVOs.MerchantOrderDetailVO;
import com.chris.vo.resultVOs.Result;
import com.chris.vo.resultVOs.ScrollResult;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chris.constant.MessageConstant.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusLogRepository orderStatusLogRepository;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private GoogleGeocodingUtil googleGeocodingUtil;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private StripeUtil stripeUtil;

    @Autowired
    private OrderEventProducer orderEventProducer;

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
                .filter(order -> order.getStatus() != OrderStatusConstant.CANCELLED && order.getStatus() != OrderStatusConstant.PENDING)
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
                            order.getPaidAt(),
                            order.getEstimateDeliveryTime(),
                            order.getTotalAmount(),
                            order.getRemark(),
                            order.getStatus(),
                            attemptedAt,
                            riderAssignmentId,
                            riderPhone,
                            order.getPayStatus()
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
    public Result<MerchantOrderDetailVO> getMerchantOrderDetail(Long userId, Long orderId) {
        Order o = orderRepository.findByOrderIdAndMerchantUserUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        MerchantOrderDetailVO vo = orderDetailMapper.toMerchantOrderDetailVO(o);
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
        return Result.success(vo);
    }

    @Override
    @Transactional
    public Result<OrderSubmitVO> submitOrder(OrderSubmitDTO dto, Long userId) {
        // 1. 校验商家存在
        Merchant merchant = merchantRepository.findById(dto.getMerchantId())
                .orElseThrow(() -> new MerchantNotFoundException("商家不存在"));

        // 2. 校验收货地址存在、归属当前用户
        AddressBook address = addressBookRepository.findById(dto.getAddressId())
                .filter(a -> a.getClient().getUser().getUserId().equals(userId))
                .orElseThrow(() -> new AddressBookNotFoundException("地址无效"));

        // 3. 计算配送距离
        String fullAddress = Stream.of(
                        address.getAddressLine1(),
                        address.getAddressLine2(),
                        address.getCity(),
                        address.getState(),
                        address.getZipcode(),
                        address.getCountry()
                )
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(", "));
        Optional<double[]> latLng = googleGeocodingUtil.fetchLatLng(fullAddress);
        double[] coords = latLng.orElseThrow(() -> new RuntimeException("未能根据地址获取经纬度"));
        double distance = GeoUtil.distance(coords[1], coords[0], merchant.getLocation());

        if (distance > 10000) {
            throw new SurpassedDeliveryDistanceException(SURPASSED_DELIVERY_DISTANCE);
        }

        // 4. 重新计算配送费（防止前端篡改）
        BigDecimal deliveryFee  = DeliveryFeeUtil.calcFee(distance);
        deliveryFee = deliveryFee.setScale(2, RoundingMode.HALF_UP);

        // 5. 检查菜品有效性（如上架状态、菜名、价格防止前端篡改等）
        // 提前查一次所有菜品，避免多次查库
        Map<Long, Dish> dishMap = dishRepository.findAllById(
                dto.getItems().stream().map(OrderItemDTO::getDishId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Dish::getDishId, d -> d));

        for (OrderItemDTO item : dto.getItems()) {
            Dish dish = dishMap.get(item.getDishId());
            if (dish == null) {
                throw new DishNotFoundException(DISH_NOT_FOUND);
            }
            if (dish.getStatus() != 1) {
                throw new DishNotOnSaleException(DISH_NOT_ON_SALE);
            }
            item.setName(dish.getName());
            item.setPrice(dish.getPrice());
            item.setSubtotal(dish.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        List<OrderItemDTO> items = dto.getItems();
        BigDecimal totalAmount = items.stream().map(OrderItemDTO::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        // 6. 生成订单主表
        Client client = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                .getClient();

        Order order = new Order();
        order.setClient(client);
        order.setMerchant(merchant);
        order.setAddress(address);
        order.setTotalAmount(totalAmount);
        order.setDeliveryFee(deliveryFee);
        order.setRemark(dto.getRemark());
        order.setStatus(OrderStatusConstant.PENDING);
        order.setPayStatus(PayStatusConstant.UNPAID);
        order.setEstimateDeliveryTime(LocalDateTime.now().plusMinutes(30));
        orderRepository.save(order);

        // 7. 保存订单明细
        for (OrderItemDTO item : dto.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setDish(dishMap.get(item.getDishId()));
            orderItem.setDishName(item.getName());
            orderItem.setUnitPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSubtotal(item.getSubtotal());
            orderItem.setRemark(item.getRemark());
            orderItemRepository.save(orderItem);
        }

        // 8. 写入订单状态流水（OrderStatusLog）
        OrderStatusLog log = new OrderStatusLog();
        log.setOrder(order);
        log.setFromStatus(null);
        log.setToStatus(OrderStatusConstant.PENDING);
        log.setChangedBy("client:" + order.getClient().getUser().getUsername());
        orderStatusLogRepository.save(log);

        OrderSubmitVO vo = new OrderSubmitVO();
        vo.setOrderId(order.getOrderId());
        return Result.success(vo);
    }

    @Override
    public Result<ScrollResult<ClientOrderVO, LocalDateTime>> scrollOrdersForClient(Long userId, ClientOrderDTO dto) {
        // 1. 查 client
        Client client = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                .getClient();

        // 2. 构建 Specification
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("client").get("clientId"), client.getClientId()));

            // 订单类型
            switch (dto.getStatusType()) {
                case "processing" -> predicates.add(cb.between(root.get("status"),
                        OrderStatusConstant.PENDING, OrderStatusConstant.DISPATCHING));
                case "completed" -> predicates.add(cb.equal(root.get("status"), OrderStatusConstant.COMPLETED));
                case "cancelled" -> predicates.add(cb.equal(root.get("status"), OrderStatusConstant.CANCELLED));
                case null, default -> throw new IllegalArgumentException("无效的statusType");
            }

            // 滚动分页条件
            if (dto.getLastCreateTime() != null && dto.getLastOrderId() != null) {
                Predicate scroll = cb.or(
                        cb.lessThan(root.get("createTime"), dto.getLastCreateTime()),
                        cb.and(
                                cb.equal(root.get("createTime"), dto.getLastCreateTime()),
                                cb.lessThan(root.get("orderId"), dto.getLastOrderId())
                        )
                );
                predicates.add(scroll);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 3. 排序+分页（最多 pageSize 条）
        Sort sort = Sort.by(Sort.Order.desc("createTime"), Sort.Order.desc("orderId"));
        Pageable pageable = PageRequest.of(0, dto.getPageSize() == null ? 5 : dto.getPageSize(), sort);

        // 4. 查询，注意：fetch join 需要在 Repository 自定义，连 riderAssignments
        List<Order> orders = orderRepository.findAll(spec, pageable).toList();

        // 5. VO 映射
        List<ClientOrderVO> voList = orders.stream().map(order -> {
            String itemNames = order.getItems() == null ? "" :
                    order.getItems().stream()
                            .map(item -> item.getDish().getName() + "x" + item.getQuantity())
                            .collect(Collectors.joining(", "));
            String merchantName = order.getMerchant() != null ? order.getMerchant().getMerchantName() : "";
            BigDecimal amount = (order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                    .add(order.getDeliveryFee() != null ? order.getDeliveryFee() : BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);

            // 派单信息
            RiderAssignment assignment = null;
            if (order.getRiderAssignments() != null && !order.getRiderAssignments().isEmpty()) {
                assignment = order.getRiderAssignments().stream()
                        .filter(a -> a.getStatus() != null && a.getStatus() == OrderStatusConstant.PICKING_UP)
                        .findFirst()
                        .orElse(null);
            }
            LocalDateTime attemptedAt = assignment != null ? assignment.getAttemptAt() : null;
            Long riderAssignmentId = assignment != null ? assignment.getRiderAssignmentId() : null;
            String riderPhone = assignment != null && assignment.getRider() != null ? assignment.getRider().getPhone() : null;

            ClientOrderVO vo = new ClientOrderVO();
            vo.setOrderId(order.getOrderId());
            vo.setItems(itemNames);
            vo.setMerchantName(merchantName);
            vo.setPaidAt(order.getPaidAt());
            vo.setEta(order.getEstimateDeliveryTime());
            vo.setAmount(amount);
            vo.setStatus(order.getStatus());
            vo.setAttemptedAt(attemptedAt);
            vo.setRiderAssignmentId(riderAssignmentId);
            vo.setRiderPhone(riderPhone);
            vo.setCreateTime(order.getCreateTime());
            return vo;
        }).toList();

        // 6. 构造滚动分页返回
        ScrollResult<ClientOrderVO, LocalDateTime> result = ScrollResult.of(
                voList,
                ClientOrderVO::getCreateTime,
                ClientOrderVO::getOrderId
        );

        return Result.success(result);
    }

    @Override
    @Transactional
    public Result<OrderPayVO> payOrder(OrderPayDTO dto, Long userId) {
        // 查订单 & 验证
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        if (!order.getClient().getUser().getUserId().equals(userId)) {
            throw new OrderPaymentException("订单不属于当前用户");
        }
        if (order.getPayStatus() != 0 || order.getStatus() != 0) {
            throw new OrderPaymentException("订单已支付或不能支付");
        }

        // 付款金额构建 （单位：cent）
        BigDecimal amount = (order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                .add(order.getDeliveryFee() != null ? order.getDeliveryFee() : BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        long amountInCents = amount.multiply(new BigDecimal(100)).longValueExact();

        String clientSecret = stripeUtil.createPaymentIntent(order.getOrderId(), userId, amountInCents, "usd");

        // 返回VO: clientSecret
        OrderPayVO vo = new OrderPayVO();

        vo.setClientSecret(clientSecret);

        return Result.success(vo);

    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, OrderCancelDTO dto) {
        Long orderId = dto.getOrderId();
        String reason = dto.getReason();
        // 1. 查询订单，校验归属
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        if (!order.getClient().getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("无权操作该订单");
        }
        // 2. 只允许进行中/待支付订单取消
        if (order.getStatus() != OrderStatusConstant.PENDING) {
            throw new IllegalStateException("订单无法取消");
        }
        changeStatus(order, OrderStatusConstant.CANCELLED, reason);
    }

    @Override
    @Transactional
    public void markOrderPaid(Long orderId, Long userId, LocalDateTime paidAt, String paymentMethod, String paymentIntentId) {
        // 1. 校验该订单属于当前用户
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getClient().getUser().getUserId().equals(userId))
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        // 幂等校验
        if (OrderStatusConstant.PAID == (order.getStatus())) {
            return;
        }

        // 2. 修改订单状态，写库
        order.setPayStatus(PayStatusConstant.PAID);
        order.setPaidAt(paidAt);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentIntentId(paymentIntentId);
        changeStatus(order, OrderStatusConstant.PAID, "");

        // 3. 推送事件
        String items = order.getItems() == null ? "" :
                order.getItems().stream()
                        .map(item -> item.getDish().getName() + "x" + item.getQuantity())
                        .collect(Collectors.joining(", "));

        OrderPaidEvent paidEvent = new OrderPaidEvent();
        paidEvent.setOrderId(orderId);
        paidEvent.setClientPhone(order.getClient().getPhone());
        paidEvent.setItems(items);
        paidEvent.setPaidAt(paidAt);
        paidEvent.setEta(order.getEstimateDeliveryTime());
        paidEvent.setAmount(order.getTotalAmount());
        paidEvent.setRemark(order.getRemark());
        paidEvent.setStatus(order.getStatus());
        paidEvent.setMerchantUserId(order.getMerchant().getUser().getUserId());

        orderEventProducer.sendOrderPaidEvent(paidEvent);
    }

    @Override
    @Transactional
    public void refundOrder(Long userId, Long orderId) {
        // 1. 校验该订单属于当前商家
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getMerchant().getUser().getUserId().equals(userId))
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        if (order.getPayStatus() != PayStatusConstant.PAID && order.getStatus() != OrderStatusConstant.CANCELLED) {
            throw new IllegalStateException("订单无法退款");
        }
        long totalAmount = order.getTotalAmount() != null ? order.getTotalAmount().multiply(new BigDecimal(100)).longValueExact() : 0;
        long deliveryFee = order.getDeliveryFee() != null ? order.getDeliveryFee().multiply(new BigDecimal(100)).longValueExact() : 0;
        long amount = totalAmount + deliveryFee;
        // 4. 调用 Stripe 退款
        String paymentIntentId = order.getPaymentIntentId();
        try {
            stripeUtil.refundPaymentIntent(paymentIntentId, amount);
            order.setPayStatus(PayStatusConstant.REFUNDING);
            orderRepository.save(order);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<ClientOrderDetailVO> getClientOrderDetail(Long userId, Long orderId) {
        // 1. 校验该订单属于当前用户
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getClient().getUser().getUserId().equals(userId))
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        ClientOrderDetailVO vo = orderDetailMapper.toClientOrderDetailVO(order);
        // 手动补充 riderPhone
        String riderPhone = order.getRiderAssignments().stream()
                // 只看骑手已接单这一步（按你的业务状态改）
                .filter(a -> a.getStatus() == OrderStatusConstant.PICKING_UP)
                // 按时间取最新一条
                .max(Comparator.comparing(RiderAssignment::getAttemptAt))
                // 拿出 Rider -> phone
                .map(RiderAssignment::getRider)
                .map(Rider::getPhone)
                .orElse(null);

        vo.setRiderPhone(riderPhone);
        return Result.success(vo);
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

        // 优先覆盖具体角色来源
        String changedBy = null;

        switch (newStatus) {
            case OrderStatusConstant.PAID -> // 客户支付
                    changedBy = "client:" + o.getClient().getUser().getUsername();
            case OrderStatusConstant.ACCEPTED, OrderStatusConstant.READY -> // 商户操作
                    changedBy = "merchant:" + o.getMerchant().getUser().getUsername();
            case OrderStatusConstant.PICKING_UP, OrderStatusConstant.DISPATCHING, OrderStatusConstant.COMPLETED -> {
                RiderAssignment ra = o.getRiderAssignments().stream()
                        .filter(a -> a.getStatus() == OrderStatusConstant.PICKING_UP)
                        .findFirst()
                        .orElse(null);
                if (ra != null && ra.getRider() != null && ra.getRider().getUser() != null) {
                    changedBy = "rider:" + ra.getRider().getUser().getUsername();
                }
            }
            default -> {
                // 取消订单情况
                if (oldStatus == OrderStatusConstant.PENDING) {
                    changedBy = "client:" + o.getClient().getUser().getUsername();
                } else if (oldStatus == OrderStatusConstant.PAID) {
                    changedBy = "merchant:" + o.getMerchant().getUser().getUsername();
                }
            }
        }

        log.setChangedBy(changedBy);
        log.setRemark(remark);
        orderStatusLogRepository.save(log);
    }
}
