package com.chris.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DishVO {
    private Long dishId;
    private CategoryVO categoryVO;
    private String name;
    private BigDecimal price;
    private Short status;
    private String image;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
