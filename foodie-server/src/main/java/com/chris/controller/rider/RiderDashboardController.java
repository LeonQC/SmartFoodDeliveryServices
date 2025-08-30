package com.chris.controller.rider;

import com.chris.context.UserContext;
import com.chris.service.OrderService;
import com.chris.vo.dashboardVOs.DashboardRiderKPIVO;
import com.chris.vo.resultVOs.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rider/dashboard")
public class RiderDashboardController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/overview")
    public Result<DashboardRiderKPIVO> getRiderDailyKPI() {
        Long userId = UserContext.getCurrentId();
        DashboardRiderKPIVO vo = orderService.getRiderDailyKPI(userId);
        return Result.success(vo);
    }
}
