package com.chris.dto;

import jakarta.persistence.Column;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShoppingCartDTO {

    private Long dishId;
    private Integer quantity;

}
