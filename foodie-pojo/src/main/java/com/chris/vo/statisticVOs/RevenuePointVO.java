package com.chris.vo.statisticVOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RevenuePointVO {
    private String date;   // 格式 "yyyy-MM-dd"
    private BigDecimal value;  // 营业额
}
