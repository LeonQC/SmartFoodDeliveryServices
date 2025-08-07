package com.chris.service;

import com.chris.dto.OrderCancelDTO;
import com.chris.dto.OrderSubmitDTO;
import com.chris.dto.OrderPayDTO;
import com.chris.dto.RejectOrderDTO;
import com.chris.dto.groups.ClientOrderDTO;
import com.chris.vo.ClientOrderVO;
import com.chris.vo.OrderPayVO;
import com.chris.vo.OrderSubmitVO;
import com.chris.vo.dashboardVOs.DashboardOngoingOrderVO;
import com.chris.vo.dashboardVOs.DashboardOrderMetricsVO;
import com.chris.vo.orderDetailVOs.ClientOrderDetailVO;
import com.chris.vo.orderDetailVOs.MerchantOrderDetailVO;
import com.chris.vo.resultVOs.Result;
import com.chris.vo.resultVOs.ScrollResult;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    DashboardOrderMetricsVO getOrderMetrics(Long userId);

    List<DashboardOngoingOrderVO> getOngoingOrders(Long userId, Short status);

    void acceptOrder(Long userId, Long orderId);

    void rejectOrder(Long userId, Long orderId, RejectOrderDTO reason);

    void readyOrder(Long userId, Long orderId);

    Result<MerchantOrderDetailVO> getMerchantOrderDetail(Long userId, Long orderId);

    Result<OrderSubmitVO> submitOrder(OrderSubmitDTO dto, Long userId);

    Result<ClientOrderDetailVO> getClientOrderDetail(Long userId, Long orderId);

    Result<ScrollResult<ClientOrderVO, LocalDateTime>> scrollOrdersForClient(Long userId, ClientOrderDTO dto);

    Result<OrderPayVO> payOrder(OrderPayDTO dto, Long userId);

    void cancelOrder(Long userId, OrderCancelDTO dto);

    void markOrderPaid(Long orderId, Long userId, LocalDateTime paidAt, String paymentMethod, String paymentIntentId);

    void refundOrder(Long userId, Long orderId);
}
