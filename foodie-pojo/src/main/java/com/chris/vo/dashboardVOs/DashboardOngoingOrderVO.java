package com.chris.vo.dashboardVOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Dashboard 专用：进行中订单简要信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOngoingOrderVO {

    // DashboardOngoingOrder必须字段

    private Long orderId;              // 订单ID
    private String clientPhone;        // 客户手机号
    private String items;              // 菜品名称列表 order -> orderItems -> dish.name
    private String itemRemarks;        // 菜品备注列表 order -> orderItems -> remark
    private LocalDateTime paidAt;      // 支付时间 (支付后5分钟未响应，则自动拒绝订单) order -> paidAt
    private LocalDateTime eta;         // 预计送达时间 order -> estimateDeliveryTime
    private BigDecimal amount;         // 实收金额 order -> totalAmount
    private String remark;             // 订单备注 order -> remark
    private Short status;              // 订单状态 order -> status
    private LocalDateTime attemptedAt; // 尝试派单时间 order -> riderAssignments -> attemptAt
    private Long riderAssignmentId;    // 骑手派单ID码 order -> riderAssignments -> riderAssignmentId
    private String riderPhone;         // 骑手手机号 order -> riderAssignments -> rider -> phone

    // Order Management 必须字段
}
