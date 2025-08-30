package com.chris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReadyEvent {
    private final String type = "ORDER_READY";
    private Long orderId;              // 订单ID
    private String merchantName;       // 商家名称
    private String merchantAddress;    // 商家地址
    private String recipientAddress;   // 收货地址
    private LocalDateTime eta;         // 预计送达时间 order -> estimateDeliveryTime
    private BigDecimal deliveryFee;    // 配送费 order -> deliveryFee
    private String remark;             // 订单备注 order -> remark

    private List<Long> riderUserIdList;
}
