package com.chris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllDishDTO {
    private Long dishId;
    private String dishName;
    private String imageUrl;
    private Long merchantId;
}
