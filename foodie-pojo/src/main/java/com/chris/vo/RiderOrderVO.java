package com.chris.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RiderOrderVO {
    private Long orderId;              // 订单ID
    private String items;              // 订单项
    private String merchantName;       // 商家名称
    private String merchantPhone;      // 商家手机号
    private String merchantAddress;    // 商家地址
    private String recipientName;      // 收货人姓名
    private String recipientPhone;     // 收货人手机号
    private String deliveryAddress;    // 配送地址
    private LocalDateTime eta;         // 预计送达时间 order -> estimateDeliveryTime
    private BigDecimal deliveryFee;    // 实收金额 order -> deliveryFee
    private String remark;             // 订单备注 order -> remark
    private Short status;              // 订单状态 order -> status
    private LocalDateTime attemptedAt; // 尝试派单时间 order -> riderAssignments -> attemptAt
    private LocalDateTime pickedUpAt;  // 订单取餐时间 order -> orderStatusLog -> changedAt (toStatus = 5)
    private LocalDateTime completedAt; // 订单完成时间 order -> orderStatusLog -> changedAt (toStatus = 6)
    private Long pickingUpId;          // 骑手取餐ID码 order -> riderAssignments -> riderAssignmentId
}