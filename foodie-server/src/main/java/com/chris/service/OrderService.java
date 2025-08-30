package com.chris.service;

import com.chris.dto.OrderCancelDTO;
import com.chris.dto.OrderSubmitDTO;
import com.chris.dto.OrderPayDTO;
import com.chris.dto.RejectOrderDTO;
import com.chris.dto.groups.ClientOrderDTO;
import com.chris.vo.*;
import com.chris.vo.dashboardVOs.DashboardMerchantKPIVO;
import com.chris.vo.dashboardVOs.DashboardRiderKPIVO;
import com.chris.vo.orderDetailVOs.ClientOrderDetailVO;
import com.chris.vo.orderDetailVOs.MerchantOrderDetailVO;
import com.chris.vo.orderDetailVOs.RiderOrderDetailVO;
import com.chris.vo.resultVOs.Result;
import com.chris.vo.resultVOs.ScrollResult;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    DashboardMerchantKPIVO getMerchantDailyKPI(Long userId);

    List<MerchantOrderVO> listOrdersForMerchant(Long userId, Short status);

    void merchantAcceptOrder(Long userId, Long orderId);

    void merchantRejectOrder(Long userId, Long orderId, RejectOrderDTO reason);

    void readyOrder(Long userId, Long orderId);

    Result<MerchantOrderDetailVO> getMerchantOrderDetail(Long userId, Long orderId);

    Result<OrderSubmitVO> submitOrder(OrderSubmitDTO dto, Long userId);

    Result<ClientOrderDetailVO> getClientOrderDetail(Long userId, Long orderId);

    Result<ScrollResult<ClientOrderVO, LocalDateTime>> scrollOrdersForClient(Long userId, ClientOrderDTO dto);

    Result<OrderPayVO> payOrder(OrderPayDTO dto, Long userId);

    void cancelOrder(Long userId, OrderCancelDTO dto);

    void markOrderPaid(Long orderId, Long userId, LocalDateTime paidAt, String paymentMethod, String paymentIntentId);

    void refundOrder(Long userId, Long orderId);

    DashboardRiderKPIVO getRiderDailyKPI(Long userId);

    List<RiderOrderVO> listOrdersForRider(Long userId, List<Short> status);

    void riderAcceptOrder(Long userId, Long orderId);

    void riderRejectOrder(Long userId, Long orderId, Short rejectType);

    void dispatchOrder(Long userId, Long orderId);

    void finishOrder(Long userId, Long orderId);

    Result<RiderOrderDetailVO> getRiderOrderDetail(Long userId, Long orderId);

    List<RiderOrderMapPointVO> listRiderOrderPoints(Long userId);
}
