package com.chris.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientOrderVO {
    private Long orderId;
    private String items;              // 菜品名称列表 order -> orderItems -> dish.name
    private String merchantName;

    private LocalDateTime paidAt;      // 支付时间 (支付后5分钟未响应，则自动拒绝订单) order -> paidAt
    private LocalDateTime eta;         // 预计送达时间 order -> estimateDeliveryTime
    private BigDecimal amount;         // 实付金额 order -> totalAmount+deliveryFee

    private Short status;              // 订单状态 order -> status
    private LocalDateTime attemptedAt; // 尝试派单时间 order -> riderAssignments -> attemptAt
    private Long riderAssignmentId;    // 骑手派单ID码 order -> riderAssignments -> riderAssignmentId
    private String riderPhone;         // 骑手手机号 order -> riderAssignments -> rider -> phone
    private LocalDateTime createTime;
}
