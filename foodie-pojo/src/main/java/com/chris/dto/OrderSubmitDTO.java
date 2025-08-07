package com.chris.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderSubmitDTO {
    private Long merchantId;             // 商户ID
    private Long addressId;              // 收货地址ID
    private List<OrderItemDTO> items;    // 订单菜品明细
    private String remark;               // 用户订单备注
    private BigDecimal deliveryFee;      // 配送费（建议由后端重新计算校验）
    private Short status = 0;            // 订单状态
    private Short payStatus = 0;         // 支付状态
}

