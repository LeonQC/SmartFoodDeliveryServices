package com.chris.dto;

import lombok.Data;

@Data
public class OrderPayDTO {
    private Long orderId;
    private String paymentMethod;
}