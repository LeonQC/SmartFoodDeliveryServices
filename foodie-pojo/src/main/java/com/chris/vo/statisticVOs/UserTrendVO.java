package com.chris.vo.statisticVOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTrendVO {
    private String date;
    private Integer all;       // 累计用户
    private Integer newClients;  // 当日新增
}
