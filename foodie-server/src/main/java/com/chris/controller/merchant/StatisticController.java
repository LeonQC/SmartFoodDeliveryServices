package com.chris.controller.merchant;

import com.chris.context.UserContext;
import com.chris.service.StatisticService;
import com.chris.vo.resultVOs.Result;
import com.chris.vo.statisticVOs.DishSalesVO;
import com.chris.vo.statisticVOs.OrderTrendVO;
import com.chris.vo.statisticVOs.RevenuePointVO;
import com.chris.vo.statisticVOs.UserTrendVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/merchant/statistics")
public class StatisticController {
    @Autowired
    private StatisticService statisticService;

    @GetMapping("/revenueTrend")
    public Result<List<RevenuePointVO>> getRevenueTrend(@RequestParam int period) {
        Long userId = UserContext.getCurrentId();
        return Result.success(statisticService.getRevenueTrend(userId, period));
    }

    @GetMapping("/userTrend")
    public Result<List<UserTrendVO>> userTrend(@RequestParam int period) {
        Long userId = UserContext.getCurrentId();
        return Result.success(statisticService.getUserTrend(userId, period));
    }

    @GetMapping("/orderTrend")
    public Result<List<OrderTrendVO>> orderTrend(@RequestParam int period) {
        Long userId = UserContext.getCurrentId();
        return Result.success(statisticService.getOrderTrend(userId, period));
    }

    @GetMapping("/topDishes")
    public Result<List<DishSalesVO>> topDishes(@RequestParam int period) {
        Long userId = UserContext.getCurrentId();
        return Result.success(statisticService.getTopDishes(userId, period));
    }
}
