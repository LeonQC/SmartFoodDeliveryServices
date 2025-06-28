package com.chris.service.impl;

import com.chris.repository.OrderItemRepository;
import com.chris.repository.OrderRepository;
import com.chris.repository.UserRepository;
import com.chris.service.StatisticService;
import com.chris.vo.statisticVOs.DishSalesVO;
import com.chris.vo.statisticVOs.OrderTrendVO;
import com.chris.vo.statisticVOs.RevenuePointVO;
import com.chris.vo.statisticVOs.UserTrendVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;

    private LocalDate getStart(int period) {
        // 包含今天，向前推 period-1 天
        return LocalDate.now().minusDays(period - 1);
    }

    @Override
    public List<RevenuePointVO> getRevenueTrend(Long userId, int period) {
        LocalDate start = getStart(period);
        return orderRepository.findRevenueTrendByUser(userId, start).stream()
                .map(row -> {
                    String        date = (String)    row[0];
                    BigDecimal    sum  = (BigDecimal) row[1];
                    // 保留两位小数
                    return new RevenuePointVO(date, sum.setScale(2, RoundingMode.HALF_UP));
                })
                .toList();
    }

    @Override
    public List<OrderTrendVO> getOrderTrend(Long userId, int period) {
        LocalDate start = getStart(period);
        return orderRepository.findOrderTrendByUser(userId, start).stream()
                .map(r -> new OrderTrendVO(
                        (String) r[0],
                        ((Number) r[1]).intValue(),
                        ((Number) r[2]).intValue()
                ))
                .toList();
    }

    @Override
    public List<UserTrendVO> getUserTrend(Long userId, int period) {
        LocalDate start = getStart(period);
        // 先取新增
        List<Object[]> rows = userRepository.findNewUserTrendByUser(userId, start);
        List<UserTrendVO> list = new ArrayList<>();
        int cumulative = 0;
        for (Object[] r : rows) {
            String date = (String) r[0];
            int today = ((Number) r[1]).intValue();
            cumulative += today;
            list.add(new UserTrendVO(date, cumulative, today));
        }
        return list;
    }

    @Override
    public List<DishSalesVO> getTopDishes(Long userId, int period) {
        LocalDate start = getStart(period);
        return orderItemRepository.findTopDishesByUser(userId, start).stream()
                .map(r -> new DishSalesVO(
                        (String) r[0],
                        ((Number) r[1]).intValue()
                ))
                .toList();
    }
}
