package com.chris.vo;

import com.chris.entity.Dish;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingCartVO {
    private Long cartId;

    private Long dishId;

    private String dishName;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal subtotal;

    private String image;

}
