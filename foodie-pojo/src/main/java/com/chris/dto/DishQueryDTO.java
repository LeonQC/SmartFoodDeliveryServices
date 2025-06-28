package com.chris.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用于条件查询菜品信息的DTO
 */
@Data
public class DishQueryDTO {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String name;
    private Short status;
    private Long categoryId;
}
