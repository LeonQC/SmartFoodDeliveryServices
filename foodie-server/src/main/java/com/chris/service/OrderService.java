package com.chris.service;

import com.chris.dto.RejectOrderDTO;
import com.chris.vo.dashboardVOs.DashboardOngoingOrderVO;
import com.chris.vo.dashboardVOs.DashboardOrderMetricsVO;
import com.chris.vo.orderDetailVOs.OrderDetailVO;

import java.util.List;

public interface OrderService {
    DashboardOrderMetricsVO getOrderMetrics(Long userId);

    List<DashboardOngoingOrderVO> getOngoingOrders(Long userId, Short status);

    void acceptOrder(Long userId, Long orderId);

    void rejectOrder(Long userId, Long orderId, RejectOrderDTO reason);

    void readyOrder(Long userId, Long orderId);

    OrderDetailVO getOrderDetail(Long userId, Long orderId);
}
