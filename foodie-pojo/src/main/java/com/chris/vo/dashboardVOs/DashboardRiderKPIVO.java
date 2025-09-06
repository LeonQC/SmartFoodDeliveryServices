package com.chris.vo.dashboardVOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRiderKPIVO {
    private BigDecimal totalIncome;
    private int completedOrders;
    private BigDecimal avgIncome;

    private int availableCount;
    private int pickingUpCount;
    private int dispatchingCount;
}
