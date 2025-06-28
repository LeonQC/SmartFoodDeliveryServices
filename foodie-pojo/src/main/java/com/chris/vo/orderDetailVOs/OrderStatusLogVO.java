package com.chris.vo.orderDetailVOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusLogVO {
    private Short fromStatus;
    private Short toStatus;
    private String changedBy;
    private String remark;
    private LocalDateTime changedAt;
}
