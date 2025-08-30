package com.chris.controller.rider;

import com.chris.context.UserContext;
import com.chris.service.OrderService;
import com.chris.vo.RiderOrderMapPointVO;
import com.chris.vo.RiderOrderVO;
import com.chris.vo.orderDetailVOs.RiderOrderDetailVO;
import com.chris.vo.resultVOs.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rider/order")
public class RiderOrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "骑手订单列表", description = "获取骑手订单列表")
    public Result<List<RiderOrderVO>> listOrdersForRider(@RequestParam List<Short> status) {
        Long userId = UserContext.getCurrentId();
        List<RiderOrderVO> orders = orderService.listOrdersForRider(userId, status);
        return Result.success(orders);
    }

    @PostMapping("/{orderId}/accept")
    @Operation(summary = "接单", description = "接单")
    public Result<String> accept(@PathVariable Long orderId) {
        orderService.riderAcceptOrder(UserContext.getCurrentId(), orderId);
        return Result.success("接单成功");
    }

    @PostMapping("/reject")
    @Operation(summary = "拒单", description = "拒单")
    public Result<String> reject(@RequestParam Long orderId, @RequestParam Short rejectType) {
        orderService.riderRejectOrder(UserContext.getCurrentId(), orderId, rejectType);
        return Result.success("拒单成功");
    }

    @PostMapping("/{orderId}/dispatch")
    @Operation(summary = "取餐成功", description = "取餐成功")
    public Result<String> dispatch(@PathVariable Long orderId) {
        orderService.dispatchOrder(UserContext.getCurrentId(), orderId);
        return Result.success("取餐成功，开始配送");
    }

    @PostMapping("/{orderId}/finish")
    @Operation(summary = "完成订单", description = "完成订单")
    public Result<String> finish(@PathVariable Long orderId) {
        orderService.finishOrder(UserContext.getCurrentId(), orderId);
        return Result.success("订单完成");
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情", description = "获取订单详情")
    public Result<RiderOrderDetailVO> getOrderDetail(@PathVariable Long orderId) {
        return orderService.getRiderOrderDetail(UserContext.getCurrentId(), orderId);
    }

    @GetMapping("/points")
    @Operation(summary = "获取餐厅和目的地经纬度", description = "获取餐厅和目的地经纬度")
    public Result<List<RiderOrderMapPointVO>> listRiderOrderPoints() {
        Long userId = UserContext.getCurrentId();
        List<RiderOrderMapPointVO> points = orderService.listRiderOrderPoints(userId);
        return Result.success(points);
    }
}