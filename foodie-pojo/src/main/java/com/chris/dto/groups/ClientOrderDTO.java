package com.chris.dto.groups;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientOrderDTO {
    private String statusType;           // "processing" | "completed" | "cancelled"
    private LocalDateTime lastCreateTime;
    private Long lastOrderId;
    private Integer pageSize;
}
