package com.chris.vo.statisticVOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderTrendVO {
    private String date;
    private Integer total;  // 当日订单总数
    private Integer valid;  // 当日有效订单数 （非取消）
}
