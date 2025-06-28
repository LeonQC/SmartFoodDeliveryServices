package com.chris.vo.statisticVOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DishSalesVO {
    private String name;    // 菜品名
    private Integer sales;  // 销量
}
