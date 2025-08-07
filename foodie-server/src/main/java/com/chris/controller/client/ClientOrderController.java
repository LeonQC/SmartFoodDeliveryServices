package com.chris.controller.client;

import com.chris.context.UserContext;
import com.chris.dto.OrderCancelDTO;
import com.chris.dto.OrderSubmitDTO;
import com.chris.dto.OrderPayDTO;
import com.chris.dto.groups.ClientOrderDTO;
import com.chris.service.OrderService;
import com.chris.vo.ClientOrderVO;
import com.chris.vo.OrderPayVO;
import com.chris.vo.OrderSubmitVO;
import com.chris.vo.orderDetailVOs.ClientOrderDetailVO;
import com.chris.vo.resultVOs.Result;
import com.chris.vo.resultVOs.ScrollResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/client/order")
public class ClientOrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public Result<OrderSubmitVO> submitOrder(@RequestBody OrderSubmitDTO dto) {
        return orderService.submitOrder(dto, UserContext.getCurrentId());
    }

    @PostMapping
    public Result<ScrollResult<ClientOrderVO, LocalDateTime>> listOrders(@RequestBody ClientOrderDTO dto) {
        return orderService.scrollOrdersForClient(UserContext.getCurrentId(), dto);
    }

    @GetMapping("/{orderId}")
    public Result<ClientOrderDetailVO> getClientOrderDetail(@PathVariable Long orderId) {
        return orderService.getClientOrderDetail(UserContext.getCurrentId(), orderId);
    }

    @PostMapping("/cancel")
    public Result<String> cancelOrder(@RequestBody OrderCancelDTO dto) {
        Long userId = UserContext.getCurrentId();
        orderService.cancelOrder(userId, dto);
        return Result.success("Order cancelled successfully");
    }

    @PostMapping("/pay")
    public Result<OrderPayVO> payOrder(@RequestBody OrderPayDTO dto) {
        Long userId = UserContext.getCurrentId();
        return orderService.payOrder(dto, userId);
    }
}
