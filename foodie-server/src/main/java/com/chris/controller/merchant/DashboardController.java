package com.chris.controller.merchant;

import com.chris.context.UserContext;
import com.chris.service.DishService;
import com.chris.service.OrderService;
import com.chris.vo.dashboardVOs.DashboardCategoryDishStatusVO;
import com.chris.vo.dashboardVOs.DashboardOrderMetricsVO;
import com.chris.vo.resultVOs.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchant/dashboard")
@Tag(name = "DashboardController", description = "Dashboard-related APIs")
public class DashboardController {
    @Autowired
    private DishService dishService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/categories")
    public Result<DashboardCategoryDishStatusVO> getCategoryDishStatus() {
        Long userId = UserContext.getCurrentId();
        DashboardCategoryDishStatusVO vo = dishService.getCategoryDishStatus(userId);
        return Result.success(vo);
    }

    @GetMapping("/orders")
    public Result<DashboardOrderMetricsVO> getOrderMetrics() {
        Long userId = UserContext.getCurrentId();
        DashboardOrderMetricsVO vo = orderService.getOrderMetrics(userId);
        return Result.success(vo);
    }
}
