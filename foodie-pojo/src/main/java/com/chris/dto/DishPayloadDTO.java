package com.chris.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用于新增，更新菜品信息的DTO
 */
@Data
public class DishPayloadDTO {
    @NotNull
    private Long categoryId;
    @NotBlank
    private String name;
    @NotNull
    private BigDecimal price;
    private String image;
    private String description;

}
