package com.chris.service.impl;

import com.chris.constant.OrderStatusConstant;
import com.chris.constant.PayStatusConstant;
import com.chris.constant.RiderAssignStatusConstant;
import com.chris.dto.*;
import com.chris.dto.groups.ClientOrderDTO;
import com.chris.entity.*;
import com.chris.exception.*;
import com.chris.mapper.OrderDetailMapper;
import com.chris.producer.OrderKafkaProducer;
import com.chris.producer.OrderRabbitMQProducer;
import com.chris.repository.*;
import com.chris.service.OrderService;
import com.chris.utils.DeliveryFeeUtil;
import com.chris.utils.GeoUtil;
import com.chris.utils.GoogleGeocodingUtil;
import com.chris.utils.StripeUtil;
import com.chris.vo.*;
import com.chris.vo.dashboardVOs.DashboardMerchantKPIVO;
import com.chris.vo.dashboardVOs.DashboardRiderKPIVO;
import com.chris.vo.orderDetailVOs.ClientOrderDetailVO;
import com.chris.vo.orderDetailVOs.MerchantOrderDetailVO;
import com.chris.vo.orderDetailVOs.RiderOrderDetailVO;
import com.chris.vo.resultVOs.Result;
import com.chris.vo.resultVOs.ScrollResult;
import com.stripe.exception.StripeException;
import jakarta.persistence.criteria.Predicate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chris.constant.MessageConstant.*;
import static com.chris.constant.RedisConstant.RIDER_LOCATION_KEY;
import static com.chris.constant.RedisConstant.RIDER_ONLINE_KEY;

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
    private OrderKafkaProducer orderKafkaProducer;

    @Autowired
    private RiderAssignmentRepository riderAssignmentRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private OrderRabbitMQProducer orderRabbitMQProducer;

    @Autowired
    private OrderEventService orderEventService;

    /**
    * 用于商家Dashboard的订单及相关数据统计
    */
    @Override
    public DashboardMerchantKPIVO getMerchantDailyKPI(Long userId) {
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

        return new DashboardMerchantKPIVO(
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
    public List<MerchantOrderVO> listOrdersForMerchant(Long userId, Short status) {
        List<Order> orders = orderRepository.findByMerchantUserIdAndStatus(userId, status);

        return orders.stream()
                .map(order -> {
                    // 拼菜品列表，菜品名称x份，逗号隔开
                    String items = order.getItems().stream()
                            .map(oi -> oi.getDish().getName() + " x" + oi.getQuantity())
                            .collect(Collectors.joining(","));

                    // 取最新派单记录的 ID 和时间
                    Long riderAssignmentId = null;
                    LocalDateTime attemptedAt = null;
                    String riderPhone = null;
                    if (status >= OrderStatusConstant.PICKING_UP) {
                        Optional<RiderAssignment> opt = order.getRiderAssignments().stream()
                                .filter(a -> a.getStatus() == RiderAssignStatusConstant.ACCEPTED)
                                .findFirst();
                        riderAssignmentId = opt.map(RiderAssignment::getRiderAssignmentId).orElse(null);
                        attemptedAt = opt.map(RiderAssignment::getAttemptAt).orElse(null);
                        riderPhone = opt.map(a -> a.getRider().getPhone()).orElse(null);
                    }

                    return new MerchantOrderVO(
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
    public void merchantAcceptOrder(Long userId, Long orderId) {
        Order o = orderRepository.findByOrderIdAndMerchantUserUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        if (o.getStatus() != OrderStatusConstant.PAID) {
            throw new OrderStatusChangeException(ORDER_STATUS_CHANGE_FAILED);
        }
        changeStatus(o, OrderStatusConstant.ACCEPTED,"");
    }

    @Override
    @Transactional
    public void merchantRejectOrder(Long userId, Long orderId, RejectOrderDTO reason) {
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
        // 1. 查订单+校验
        Order o = orderRepository.findByOrderIdAndMerchantUserUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        if (o.getStatus() != OrderStatusConstant.ACCEPTED) {
            throw new OrderStatusChangeException(ORDER_STATUS_CHANGE_FAILED);
        }
        changeStatus(o, OrderStatusConstant.READY,"");

        // 2. 查商家坐标
        Merchant merchant = o.getMerchant();
        double merchantLng = merchant.getLongitude();
        double merchantLat = merchant.getLatitude();

        // 3. 查询骑手（geo查+set过滤），优先在redis里操作
        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        List<RedisGeoCommands.GeoLocation<String>> candidates  =
                Objects.requireNonNull(geoOps.radius(
                        RIDER_LOCATION_KEY,
                        new Circle(new Point(merchantLng, merchantLat), new Distance(1, Metrics.KILOMETERS)),
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().limit(10)
                )).getContent().stream().map(GeoResult::getContent).toList();

        // 查询哪些骑手已被推单（Set查询，防止重复推送）
        Set<Long> alreadyAssigned = new HashSet<>(riderAssignmentRepository
                .findRiderUserUserIdsByOrderOrderId(orderId));

        List<Long> newRiderUserIdList = new ArrayList<>();
        for (RedisGeoCommands.GeoLocation<String> loc : candidates) {
            String riderIdStr = loc.getName();
            Long riderUserId = Long.valueOf(riderIdStr);
            // 仅选在线、且没被推过单的
            if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(RIDER_ONLINE_KEY, riderIdStr))
                    && !alreadyAssigned.contains(riderUserId)) {
                newRiderUserIdList.add(riderUserId);
                if (newRiderUserIdList.size() >= 3) break;
            }
        }
        if (newRiderUserIdList.isEmpty()) {
            throw new RuntimeException("周围暂无可用骑手，请稍后再试");
        }

        // 4. 批量写入RiderAssignment（不重复插入）
        for (Long riderUserId : newRiderUserIdList) {
            RiderAssignment assignment = new RiderAssignment();
            assignment.setOrder(o);
            assignment.setRider(
                    userRepository.findById(riderUserId)
                            .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND)).getRider()
            );
            assignment.setAttemptAt(LocalDateTime.now());
            // status=null表示待响应
            riderAssignmentRepository.save(assignment);
        }

        // 5. 组装消息并推送kafka
        String recipientAddress = o.getAddress().getAddressLine1();
        if (StringUtils.hasText(o.getAddress().getAddressLine2())) {
            recipientAddress += o.getAddress().getAddressLine2();
        }

        OrderReadyEvent event = new OrderReadyEvent();
        event.setOrderId(o.getOrderId());
        event.setMerchantName(merchant.getMerchantName());
        event.setMerchantAddress(merchant.getAddress());
        event.setRecipientAddress(recipientAddress);
        event.setEta(o.getEstimateDeliveryTime());
        event.setDeliveryFee(o.getDeliveryFee());
        event.setRemark(o.getRemark());
        event.setRiderUserIdList(newRiderUserIdList);

        orderKafkaProducer.sendOrderReadyEvent(event);
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
                .filter(a -> a.getStatus() == RiderAssignStatusConstant.ACCEPTED)
                // 按时间取最新一条
                .findFirst()
                // 拿出 Rider -> phone
                .map(RiderAssignment::getRider)
                .map(Rider::getPhone)
                .orElse(null);

        vo.setRiderPhone(riderPhone);
        return Result.success(vo);
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
        order.setEstimateDeliveryTime(LocalDateTime.now().plusMinutes(dto.getEtaMinutes()));
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

        // 8.1 发送延迟关单消息
        orderRabbitMQProducer.sendOrderDelayMessage(order.getOrderId());

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
                        .filter(a -> a.getStatus() != null && a.getStatus() == RiderAssignStatusConstant.ACCEPTED)
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

        orderKafkaProducer.sendOrderPaidEvent(paidEvent);
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
                .filter(a -> a.getStatus() == RiderAssignStatusConstant.ACCEPTED)
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
    public DashboardRiderKPIVO getRiderDailyKPI(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        // 查询今天该骑手的所有分配记录
        List<RiderAssignment> assignments = riderAssignmentRepository
                .findByRiderUserUserIdAndAttemptAtBetween(userId, start, end);

        // 获取今天该骑手所有接取的订单
        List<Order> todayOrders = assignments.stream()
                .filter(a -> a.getStatus() != null &&  a.getStatus() == RiderAssignStatusConstant.ACCEPTED)
                .map(RiderAssignment::getOrder)
                .distinct()
                .toList();

        int completedOrders = 0;
        BigDecimal totalIncome = BigDecimal.ZERO;
        int availableCount = 0;
        int pickingUpCount = 0;
        int dispatchingCount = 0;

        for (Order order : todayOrders) {
            Short status = order.getStatus();
            BigDecimal fee = order.getDeliveryFee() != null ? order.getDeliveryFee() : BigDecimal.ZERO;

            // 已完成订单
            if (status == OrderStatusConstant.COMPLETED) {
                completedOrders++;
                totalIncome = totalIncome.add(fee);
            }

            if (status == OrderStatusConstant.PICKING_UP) pickingUpCount++;
            if (status == OrderStatusConstant.DISPATCHING) dispatchingCount++;
        }

        BigDecimal avgIncome = completedOrders > 0
                ? totalIncome.divide(BigDecimal.valueOf(completedOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new DashboardRiderKPIVO(
                totalIncome,
                completedOrders,
                avgIncome,
                availableCount,
                pickingUpCount,
                dispatchingCount
        );
    }

    @Override
    public List<RiderOrderVO> listOrdersForRider(Long userId, List<Short> statusList) {
        // 一次性查出所有订单
        List<Order> orderList = orderRepository.findByRiderAssignmentsRiderUserUserIdAndStatusIn(userId, statusList);

        return orderList.stream().map(order -> {
            RiderOrderVO vo = new RiderOrderVO();
            vo.setOrderId(order.getOrderId());

            // 拼菜品列表，菜品名称x份，逗号隔开
            String items = order.getItems().stream()
                    .map(oi -> oi.getDish().getName() + " x" + oi.getQuantity())
                    .collect(Collectors.joining(","));

            vo.setItems(items);
            vo.setMerchantName(order.getMerchant() != null ? order.getMerchant().getMerchantName() : null);
            vo.setMerchantPhone(order.getMerchant() != null ? order.getMerchant().getPhone() : null);
            vo.setMerchantAddress(order.getMerchant() != null ? order.getMerchant().getAddress() : null);
            vo.setRecipientName(order.getAddress() != null ? order.getAddress().getRecipient() : null);
            vo.setRecipientPhone(order.getAddress() != null ? order.getAddress().getPhone() : null);
            vo.setDeliveryAddress(order.getAddress() != null ? order.getAddress().getAddressLine1() : null);

            vo.setEta(order.getEstimateDeliveryTime());
            vo.setDeliveryFee(order.getDeliveryFee());
            vo.setRemark(order.getRemark());
            vo.setStatus(order.getStatus());

            // 派单时间/ID：从 order.getRiderAssignments() 找出本骑手的 assignment
            RiderAssignment assignment = order.getRiderAssignments().stream()
                    .filter(ra -> ra.getRider() != null && ra.getRider().getUser().getUserId().equals(userId))
                    .findFirst().orElse(null);
            vo.setAttemptedAt(assignment != null ? assignment.getAttemptAt() : null);
            vo.setPickingUpId(assignment != null ? assignment.getRiderAssignmentId() : null);

            // pickedUpAt (取餐时间，toStatus=5) 和 completedAt (toStatus=6)
            if (order.getStatusLogs() != null) {
                vo.setPickedUpAt(
                        order.getStatusLogs().stream()
                                .filter(log -> OrderStatusConstant.DISPATCHING == log.getToStatus())
                                .map(OrderStatusLog::getChangedAt)
                                .findFirst()
                                .orElse(null)
                );
                vo.setCompletedAt(
                        order.getStatusLogs().stream()
                                .filter(log -> OrderStatusConstant.COMPLETED ==log.getToStatus())
                                .map(OrderStatusLog::getChangedAt)
                                .findFirst()
                                .orElse(null)
                );
            }
            return vo;
        }).toList();
    }

    @Override
    @Transactional
    public void riderAcceptOrder(Long userId, Long orderId) {
        String lockKey = "order:accept:" + orderId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            // 1. 尝试加锁（3秒过期，1秒内抢锁）
            locked = lock.tryLock(1, 3, TimeUnit.SECONDS);
            if (!locked) {
                throw new RuntimeException("抢单太慢啦，请稍后重试");
            }

            // 2. 查订单
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException("订单不存在"));

            // 3. 检查订单状态是否可抢（3=已就绪，骑手可抢单）
            if (order.getStatus() != OrderStatusConstant.READY) {
                throw new RuntimeException("订单已被其他骑手抢走");
            }

            // 4. 检查是否已有分配记录
            boolean alreadyAssigned = riderAssignmentRepository.existsByOrderOrderIdAndStatus(orderId, RiderAssignStatusConstant.ACCEPTED);
            if (alreadyAssigned) {
                throw new RuntimeException("订单已被成功分配");
            }

            // 5. 获取骑手信息
            Rider rider = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                    .getRider();

            // 6. 写入分配表
            RiderAssignment assignment = new RiderAssignment();
            assignment.setOrder(order);
            assignment.setRider(rider);
            assignment.setAttemptAt(LocalDateTime.now());
            assignment.setStatus(RiderAssignStatusConstant.ACCEPTED);
            riderAssignmentRepository.save(assignment);

            // 7. 更新订单状态为“配送中”或你业务逻辑定义的下一个状态
            order.setStatus(OrderStatusConstant.PICKING_UP);
            order.getRiderAssignments().add(assignment);
            orderRepository.save(order);

        } catch (InterruptedException e) {
            throw new RuntimeException("抢单失败，系统繁忙", e);
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional
    public void riderRejectOrder(Long userId, Long orderId, Short rejectType) {
        // 1. 查找RiderAssignment
        RiderAssignment assignment = riderAssignmentRepository.findByOrderOrderIdAndRiderUserUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("没有找到分配记录"));

        // 2. 状态校验
        if (assignment.getStatus() != null) {
            throw new RuntimeException("订单已被操作，无法拒单");
        }

        // 3. 设置为拒绝
        if (RiderAssignStatusConstant.REJECTED == rejectType) {
            assignment.setStatus(RiderAssignStatusConstant.REJECTED);
        } else if (RiderAssignStatusConstant.NO_RESPONSE == rejectType) {
            assignment.setStatus(RiderAssignStatusConstant.NO_RESPONSE);
        } else {
            throw new RuntimeException("未知拒绝类型");
        }
        assignment.setAttemptAt(LocalDateTime.now());
        riderAssignmentRepository.save(assignment);

        // 4. 查询同订单所有 assignment
        List<RiderAssignment> allAssignments = riderAssignmentRepository.findAllByOrderOrderId(orderId);

        boolean allRejected = allAssignments
                .stream()
                .allMatch(a -> a.getStatus() == RiderAssignStatusConstant.REJECTED || a.getStatus() == RiderAssignStatusConstant.NO_RESPONSE);

        if (allRejected) {
            Order order = allAssignments.getFirst().getOrder(); // 复用已有对象
            Long merchantUserId = order.getMerchant().getUser().getUserId();

            // 触发重新推单
            this.readyOrder(merchantUserId,orderId);
        }
    }

    @Override
    public void dispatchOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getRiderAssignments().stream()
                        .anyMatch(ra -> ra.getRider().getUser().getUserId().equals(userId)))
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        if (order.getStatus() != OrderStatusConstant.PICKING_UP) {
            throw new OrderStatusChangeException(ORDER_STATUS_CHANGE_FAILED);
        }
        changeStatus(order, OrderStatusConstant.DISPATCHING,"");
    }

    @Override
    public void finishOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getRiderAssignments().stream()
                        .anyMatch(ra -> ra.getRider().getUser().getUserId().equals(userId)))
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        if (order.getStatus() != OrderStatusConstant.DISPATCHING) {
            throw new OrderStatusChangeException(ORDER_STATUS_CHANGE_FAILED);
        }
        changeStatus(order, OrderStatusConstant.COMPLETED,"");

        orderKafkaProducer.sendOrderCompletedEvent(orderEventService.buildOrderCompletedEvent(order));
    }

    @Override
    public Result<RiderOrderDetailVO> getRiderOrderDetail(Long userId, Long orderId) {
        // 1. 查询订单，判断该骑手是否有权访问
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 判断该骑手是否和此订单有关（已分配/已接单/已完成等），可根据你的业务需求调整
        boolean hasAuth = order.getRiderAssignments().stream()
                .anyMatch(ra -> ra.getRider().getUser().getUserId().equals(userId));
        if (!hasAuth) throw new RuntimeException("无权限访问该订单");

        RiderOrderDetailVO vo = orderDetailMapper.toRiderOrderDetailVO(order);
        return Result.success(vo);
    }

    @Override
    public List<RiderOrderMapPointVO> listRiderOrderPoints(Long userId) {
        List<RiderAssignment> assignments = riderAssignmentRepository.findAllByRiderUserUserId(userId);
        if (assignments.isEmpty()) {
            throw new OrderNotFoundException(ORDER_NOT_FOUND);
        }

        // 只取配送中和待取餐的订单
        return assignments.stream()
                .filter(a -> a.getStatus() != null && a.getStatus() == RiderAssignStatusConstant.ACCEPTED)
                .map(RiderAssignment::getOrder)
                .filter(o -> o.getStatus() == OrderStatusConstant.PICKING_UP || o.getStatus() == OrderStatusConstant.DISPATCHING)
                .map(order -> {
                    String destination = Stream.of(
                                    order.getAddress().getAddressLine1(),
                                    order.getAddress().getAddressLine2(),
                                    order.getAddress().getCity(),
                                    order.getAddress().getState(),
                                    order.getAddress().getZipcode(),
                                    order.getAddress().getCountry()
                            )
                            .filter(Objects::nonNull)
                            .filter(s -> !s.isBlank())
                            .collect(Collectors.joining(", "));
                    Optional<double[]> latLng = googleGeocodingUtil.fetchLatLng(destination);
                    double[] coords = latLng.orElseThrow(() -> new RuntimeException("未能根据地址获取经纬度"));
                    RiderOrderMapPointVO vo = new RiderOrderMapPointVO();
                    vo.setOrderId(order.getOrderId());
                    vo.setMerchantName(order.getMerchant().getMerchantName());
                    vo.setMerchantLng(order.getMerchant().getLongitude());
                    vo.setMerchantLat(order.getMerchant().getLatitude());
                    vo.setDestinationName(order.getAddress().getRecipient() + " " + order.getAddress().getLabel());
                    vo.setDestinationLng(coords[1]);
                    vo.setDestinationLat(coords[0]);
                    return vo;
                })
                .toList();
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
                        .filter(a -> a.getStatus() == RiderAssignStatusConstant.ACCEPTED)
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
