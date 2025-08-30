package com.chris.vo;

import lombok.Data;

@Data
public class RecommendItemVO {
    private Long dishId;
    private String dishName;
    private String imageUrl;
    private Long merchantId;      // 临时，AI输出
    private String merchantUrl;
}
