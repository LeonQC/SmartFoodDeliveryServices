package com.chris.controller.merchant;

import com.chris.context.UserContext;
import com.chris.dto.RejectOrderDTO;
import com.chris.service.OrderService;
import com.chris.vo.dashboardVOs.DashboardOngoingOrderVO;
import com.chris.vo.orderDetailVOs.OrderDetailVO;
import com.chris.vo.resultVOs.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant/orders")
@Tag(name = "OrderController", description = "Order-related APIs")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "Query ongoing orders", description = "Query orders for the current merchant by status, used by merchant dashboard")
    public Result<List<DashboardOngoingOrderVO>> getOngoingOrders(@RequestParam Short status) {
        Long userId = UserContext.getCurrentId();
        List<DashboardOngoingOrderVO> orders = orderService.getOngoingOrders(userId, status);
        return Result.success(orders);
    }

    @PostMapping("/{orderId}/accept")
    @Operation(summary = "Merchant accepts an order", description = "Change order status from PAID to ACCEPTED")
    public Result<String> acceptOrder(@PathVariable Long orderId) {
        Long userId = UserContext.getCurrentId();
        orderService.acceptOrder(userId, orderId);
        return Result.success("Order accepted");
    }

    @PostMapping("/{orderId}/reject")
    @Operation(summary = "Merchant rejects an order", description = "Change order status from PAID to CANCELLED")
    public Result<String> reject(@PathVariable Long orderId, @RequestBody RejectOrderDTO reason) {

        // TODO: trigger refund to client.

        Long userId = UserContext.getCurrentId();
        orderService.rejectOrder(userId, orderId, reason);
        return Result.success("Order rejected");
    }

    @PostMapping("/{orderId}/ready")
    @Operation(summary = "Merchant marks an order ready", description = "Change order status from ACCEPTED to READY")
    public Result<String> ready(@PathVariable Long orderId) {

        // TODO: push the order to the available rider.

        Long userId = UserContext.getCurrentId();
        orderService.readyOrder(userId, orderId);
        return Result.success("Operation completed successfully");
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order detail", description = "Merchant view all the details of an order")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long orderId) {
        Long userId = UserContext.getCurrentId();
        OrderDetailVO orderDetailVO = orderService.getOrderDetail(userId, orderId);
        return Result.success(orderDetailVO);
    }
}
