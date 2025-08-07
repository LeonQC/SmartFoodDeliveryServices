package com.chris.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryFeeVO {
    private BigDecimal deliveryFee;
    private Double distance;
}
