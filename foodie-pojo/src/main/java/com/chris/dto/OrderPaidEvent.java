package com.chris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaidEvent {
    private String type = "NEW_ORDER";
    private Long orderId;              // 订单ID
    private String clientPhone;        // 客户手机号
    private String items;              // 菜品名称列表 order -> orderItems -> dish.name
    private LocalDateTime paidAt;      // 支付时间 (支付后5分钟未响应，则自动拒绝订单) order -> paidAt
    private LocalDateTime eta;         // 预计送达时间 order -> estimateDeliveryTime
    private BigDecimal amount;         // 实收金额 order -> totalAmount
    private String remark;             // 订单备注 order -> remark
    private Short status;              // 订单状态 order -> status

    private Long merchantUserId;           // 商家ID order -> merchant -> user -> userId
}