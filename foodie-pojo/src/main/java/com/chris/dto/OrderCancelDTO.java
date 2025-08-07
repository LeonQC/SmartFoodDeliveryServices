package com.chris.dto;

import lombok.Data;

@Data
public class OrderCancelDTO {
    private Long orderId;
    private String reason;
}
