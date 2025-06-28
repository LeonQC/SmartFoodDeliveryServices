package com.chris.service;

import com.chris.vo.statisticVOs.DishSalesVO;
import com.chris.vo.statisticVOs.OrderTrendVO;
import com.chris.vo.statisticVOs.RevenuePointVO;
import com.chris.vo.statisticVOs.UserTrendVO;

import java.util.List;

public interface StatisticService {
    List<RevenuePointVO> getRevenueTrend(Long userId, int period);
    List<OrderTrendVO> getOrderTrend(Long userId, int period);
    List<UserTrendVO> getUserTrend(Long userId, int period);
    List<DishSalesVO> getTopDishes(Long userId, int period);
}
