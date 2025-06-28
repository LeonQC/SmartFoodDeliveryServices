package com.chris.vo.dashboardVOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于商家Dashboard页面的分类与菜品数据状态的统计
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardCategoryDishStatusVO {
    // 分类总数与启用数
    private long categoryTotal;
    private long categoryActive;
    // 菜品总数与启用数
    private long dishTotal;
    private long dishActive;
}
