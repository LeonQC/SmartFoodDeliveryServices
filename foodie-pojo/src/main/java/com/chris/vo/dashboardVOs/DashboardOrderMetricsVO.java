package com.chris.vo.dashboardVOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用于商家 Dashboard 中订单及相关指标的数据统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOrderMetricsVO {
    // 今日营业额
    private BigDecimal revenue;
    // 今日有效订单数（已支付且未取消的）
    private long validOrders;
    // 今日订单平均客单价
    private BigDecimal avgPrice;
    // 今日订单完成率 = completed / total * 100
    private double completionRate;
    // 今日新用户数
    private long newUsers;

    // 当前待接单数量（status = 1）
    private long paidCount;
    // 当前准备中数量 （status = 2）
    private long preparingCount;
    // 当前派发骑手数量（status = 3）
    private long readyToGoCount;
    // 当前待取餐数量（status = 4）
    private long pickingUpCount;
    // 当前派送中数量（status = 5）
    private long dispatchingCount;
    // 已完成数量（status = 6）
    private long completedCount;
    // 已取消数量（status = 7）
    private long cancelledCount;
    // 全部订单数（status in [0..7]）
    private long totalCount;
}
